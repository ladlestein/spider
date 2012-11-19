package spider.mineral

import com.nowanswers.spider.Utilities._
import scala.xml

/**
 * Created with IntelliJ IDEA.
 * User: ladlestein
 * Date: 7/9/12
 * Time: 6:30 PM
 *
 * Defines the mineral scraping components
 */


trait MineralPageScraperComponent {

  def scraper: MineralPageScraper

  trait MineralPageScraper {
    def findTitle(doc: xml.Elem): Option[String]

    def findFormulaText(doc: xml.Elem): Option[String]
  }
}

trait MindatMineralPageScraperComponent extends MineralPageScraperComponent {

  val scraper = new MineralPageScraper {
    def findTitle(doc: xml.Elem) = {
      val parentO = findByAttribute (doc \\ "div", "class", "fpbox990nl")
      parentO map { node =>
        ( ( node \\ "h1" ) head ) text
      }
    }

    def findFormulaText(doc: xml.Elem) = {
      val seq = doc \\ "table"
      val parentO = findByAttribute (seq, "class", "mindatasimple")
      val rawText = parentO map {
        node =>
          ( ( node \\ "td" ) head ) text
      }
      rawText map { text =>
      // Get rid of spaces and non-breaking spaces
        text.replaceAll(" ", "").replaceAll(160.toChar.toString, "")
      }
    }

  }
}

