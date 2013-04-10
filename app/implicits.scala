/**
 * Created with IntelliJ IDEA.
 * User: ladlestein
 * Date: 4/10/13
 * Time: 1:10 AM
 * To change this template use File | Settings | File Templates.
 */

package spider

import play.api.libs.concurrent.Akka
import scala.concurrent.ExecutionContext
import akka.util.Timeout
import scala.concurrent.duration._
import play.api.Play.current

package object implicits {

  val dispatcher = Akka.system.dispatcher

  implicit val ec = ExecutionContext.fromExecutor(dispatcher)

  implicit val timeout = Timeout(5 seconds)

}
