package spider

import akka.routing.RoundRobinRouter
import akka.util.duration._
import java.io.{PrintWriter, File}
import play.api.libs.ws.WS
import play.api.{Logger, Play}
import play.api.Play.current
import akka.actor.{ActorRef, Props, ActorSystem, Actor}
import play.api.libs.concurrent.Akka
import akka.dispatch.{ExecutionContext, Future}
import akka.pattern._
import akka.actor.Status.Success
import akka.util.Timeout

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
  val dispatcher = Akka.system.dispatcher
  implicit val ec = ExecutionContext.fromExecutor(dispatcher)

  implicit val timeout = Timeout(5 seconds)


  val master = Akka.system.actorOf(Props[Master], name = "Master")

  def run() = {
    master ? Start
  }


  class Master extends Actor {

    val topLevelFetchers = context.actorOf(Props[LetterFetcher].withRouter(RoundRobinRouter(nLetterFetchers)), name = "letterRouter")

    protected def receive = {
      case Start => {
        val cs = sender
        val tasks = Future.sequence(
          for (letter <- 'A' to 'Z')
            yield topLevelFetchers ? FetchLetter(letter)
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

  class LetterFetcher extends Actor {
    protected def receive = {
      case FetchLetter(letter) => {
        val cs = sender
        val url = letterPage format letter
        val resP = WS.url(url).get
        resP map {
          res => {
            val body = res.body
            val file = new File(cacheDir, letter + ".html")
            new PrintWriter(file) print body
            println("replying to " + cs)
              cs ! Success
          }
        }
      }
    }
  }
}

sealed trait SpiderMessage

case class Start(listener: ActorRef) extends SpiderMessage
case object Finished extends SpiderMessage
case class FetchLetter(letter: Char) extends SpiderMessage
case class CachePage(letter: Char, body: String, listener: ActorRef) extends SpiderMessage


