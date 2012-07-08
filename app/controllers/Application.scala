package controllers

import play.api._
import libs.concurrent._
import play.api.mvc._
import spider.Spider
import akka.actor.Props
import akka.util.Duration

object Application extends Controller {

  def index = Action {
    Async {
      val start = System.currentTimeMillis

      (Spider.run.asPromise) map { _ =>
        Ok("That took " + Duration(System.currentTimeMillis - start, java.util.concurrent.TimeUnit.MILLISECONDS).toMillis )
      }
    }
  }


}