package spider

import akka.routing.RoundRobinRouter
import akka.util.duration._
import java.io.{PrintWriter, File}
import mineral.{RealMineralBuilderComponent, MineralBuilderComponent}
import play.api.libs.ws.WS
import play.api.{Logger, Play}
import play.api.Play.current
import akka.actor.{ActorRef, Props, ActorSystem, Actor}
import play.api.libs.concurrent.Akka
import akka.dispatch.{ExecutionContext, Future}
import akka.dispatch.Future.flow
import akka.pattern._
import akka.actor.Status.Success
import akka.util.Timeout
import org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl
import java.net.URL
import xml.Elem
import com.nowanswers.chemistry.BasicFormulaParserComponent

/**
 * Created with IntelliJ IDEA.
 * User: ladlestein
 * Date: 6/11/12
 * Time: 6:34 PM
 * To change this template use File | Settings | File Templates.
 */

object Spider {

  val letterPage = "http://www.mindat.org/index-%s.html"
  val cacheDir = "/Users/ladlestein/cache"
  new File(cacheDir).mkdir

  val nLetterFetchers = 2
  val nMineralVisitors = 5

  val dispatcher = Akka.system.dispatcher
  implicit val ec = ExecutionContext.fromExecutor(dispatcher)

  implicit val timeout = Timeout(5 seconds)


  val parserFactory = new SAXFactoryImpl

  val master = Akka.system.actorOf(Props[Master], name = "Master")

  val mineralFetchers = Akka.system.actorOf(Props[MineralVisitor].withRouter(RoundRobinRouter(nMineralVisitors)), name = "mineralVisitor")

  def run() = {
    master ? Start
  }
  class Master extends Actor {

    val topLevelFetchers = context.actorOf(Props[PageVisitor].withRouter(RoundRobinRouter(nLetterFetchers)), name = "letterRouter")

    protected def receive = {
      case Start => {
        val cs = sender
        val tasks = Future.sequence(
          for (letter <- 'A' to 'B')
            yield {
              val url = letterPage format letter
              topLevelFetchers ? VisitPage(url)
            }
        )
        tasks onComplete { _ =>
          {
            println("all tasks complete")
            cs ! Success
          }
        }
      }
    }
  }

  class PageVisitor extends Actor {

    val parser = parserFactory.newSAXParser

    protected def receive = {
      case VisitPage(url) => {
        val cs = sender
        val resP = WS.url(url).get

        resP map {
          res => {
            val body = res.body
            println("have body for " + url)

            try {
              val loader = xml.XML.withSAXParser(parser)
              val doc = loader.loadString(body)
              val seq = doc \\ "a"

              seq filter { elem =>
                ! ((elem \ "b") isEmpty)
              } map { elem =>
                elem \ "@href"
              } slice (0, 5) foreach { href =>
                val mineralPageUrl = new URL(new URL(url), href.text)
                mineralFetchers ? VisitMineral(mineralPageUrl.toString)
              }

              println("# of links in " + url + " is " + seq.length)
            }
            catch {
              // TODO use logger
              case x => println("Exception: " + x)
              throw x
            }
            cs ! Success
          }
        }
      }
    }
  }

  class MineralVisitor extends Actor with RealMineralBuilderComponent with BasicFormulaParserComponent {

    val saxparser = parserFactory.newSAXParser


    protected def receive = {
      case VisitMineral(url) => {
        val cs = sender
        val resP = WS.url(url).get
        resP map {
          res => {
            val body = res.body
            println("have body for " + url)

            try {
              val loader = xml.XML.withSAXParser(saxparser)
              val doc = loader.loadString(body)
              (builder buildMineral doc) foreach { mineral =>
                println("Found mineral %s with formula %s".format(mineral.name, mineral.formula))
                mineral
              }

            }

            catch {
              // TODO use logger
              case x => println("Exception: " + x)
              throw x
            }
            cs ! Success

          }
        }
      }
    }

//    def findFormula(elem: Elem) = {
//      (elem \\ "th").find {
//        node => node match
//        {
//          case <th>{ text }</th> if text.toString == "Formula:" => true
//        }
//      } flatMap {
//        formulaNode => formulaNode.
//      }
//    }
//
//    def findName(doc: Elem) {
//      val nodes = doc \\ "h1"
//      val name = nodes.head.text
//      println("found mineral: " + name)
//    }
  }
}

sealed trait SpiderMessage

case class Start(listener: ActorRef) extends SpiderMessage
case object Flow extends SpiderMessage

case object Finished extends SpiderMessage
case class VisitPage(url: String) extends SpiderMessage
case class VisitMineral(url: String) extends SpiderMessage
case class CachePage(letter: Char, body: String, listener: ActorRef) extends SpiderMessage


