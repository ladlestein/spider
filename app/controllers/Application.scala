package controllers

import play.api.mvc._
import scala.concurrent.duration._
import spider._
import spider.implicits._

object Application extends Controller {

  def index = Action {
    Async {
      val start = System.currentTimeMillis

      (Spider.run) map { _ =>
        Ok("That took " + Duration(System.currentTimeMillis - start, java.util.concurrent.TimeUnit.MILLISECONDS).toMillis )
      }
    }
  }


}