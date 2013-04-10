package spider

import akka.routing.RoundRobinRouter
import java.io.File
import mineral.MindatMineralPageScraperComponent
import play.api.libs.ws.WS
import play.api.Play.current
import akka.actor.{ActorLogging, ActorRef, Props, Actor}
import play.api.libs.concurrent.Akka
import akka.pattern._
import org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl
import java.net.URL
import com.nowanswers.chemistry.BasicFormulaParserComponent
import com.nowanswers.spider.{MongoMineralStoreComponent, RealMineralBuilderComponent}
import com.mongodb.casbah.Imports._
import akka.actor.Status.Success
import scala.concurrent.{Future, ExecutionContext}
import spider.implicits._

/**
 * Created with IntelliJ IDEA.
 * User: ladlestein
 * Date: 6/11/12
 * Time: 6:34 PM
 *
 * The spidering code is here.
 */

object Spider {

  val letterPage = "http://www.mindat.org/index-%s.html"
  val cacheDir = "/Users/ladlestein/cache"
  new File(cacheDir).mkdir

  val nLetterFetchers = 2
  val nMineralVisitors = 5

  val parserFactory = new SAXFactoryImpl

  val master = Akka.system.actorOf(Props[Master], name = "Master")

  val mineralFetchers = Akka.system.actorOf(Props[MineralVisitor].withRouter(RoundRobinRouter(nMineralVisitors)), name = "mineralVisitor")

  def run = {
    master ? Start
  }

  class Master extends Actor with ActorLogging {

    val topLevelFetchers = context.actorOf(Props[PageVisitor].withRouter(RoundRobinRouter(nLetterFetchers)), name = "letterRouter")

    def receive = {
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
            log.info("all letter-pages fetched")
            cs ! Success
          }
        }
      }
    }
  }

  class PageVisitor extends Actor with ActorLogging {

    val parser = parserFactory.newSAXParser

    def receive = {
      case VisitPage(url) => {
        val cs = sender
        val resP = WS.url(url).get()

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
              } foreach { href =>
                val mineralPageUrl = new URL(new URL(url), href.text)
                mineralFetchers ? VisitMineral(mineralPageUrl.toString)
              }

              println("# of links in " + url + " is " + seq.length)
            }
            catch {
              // TODO use logger
              case x: Exception => log.error(x, s"Exception thrown processing letter page $url")
              throw x
            }
            cs ! Success
          }
        }
      }
    }
  }

  class MineralVisitor extends Actor with ActorLogging

  with RealMineralBuilderComponent
  with BasicFormulaParserComponent
  with MindatMineralPageScraperComponent
  with MongoMineralStoreComponent {

    val saxparser = parserFactory.newSAXParser

    val collection = {
      val connection = MongoConnection()
      val database = connection("spider")
      database ("minerals")
    }


    def receive = {
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
                store storeMineral mineral
              }
            }

            catch {
              // TODO use logger
              case x: Exception => log.error(x, s"Exception thrown processing mineral page $url")
              throw x
            }
            cs ! Success

          }
        }
      }
    }

  }
}

sealed trait SpiderMessage

case class Start(listener: ActorRef) extends SpiderMessage
case object Flow extends SpiderMessage

case object Finished extends SpiderMessage
case class VisitPage(url: String) extends SpiderMessage
case class VisitMineral(url: String) extends SpiderMessage
case class CachePage(letter: Char, body: String, listener: ActorRef) extends SpiderMessage


