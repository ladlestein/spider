package com.nowanswers.spider

import org.specs2.mutable._
import org.specs2.specification.Scope
import org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl
import org.specs2.mock.Mockito
import spider.mineral.MindatMineralPageScraperComponent

/**
 * Created with IntelliJ IDEA.
 * User: ladlestein
 * Date: 6/23/12
 * Time: 9:57 PM
 *
 * Specifies the behavior of the Mindat mineral page scraper.
 */

class MindatScrapingSpec extends Specification with MindatMineralPageScraperComponent with Mockito {

  val parserFactory = new SAXFactoryImpl
  val testParser = parserFactory.newSAXParser
  val stream = scala.io.Source.fromInputStream (getClass.getClassLoader.getResourceAsStream ("svyazhinite.html"))
  val loader = xml.XML.withSAXParser(testParser)
  val doc = loader.loadString(stream.mkString)

  val bigFormulaText = "(Mg,Mn2+,Ca)(Al,Fe3+)(SO4)2F·14H2O"

  "The mineral scraper" should {


    "find the title of the mineral" in new Scope {
      val Some(title) = scraper findTitle doc
      title must_== "Svyazhinite"
    }

    "find the chemical formula text for the mineral" in new Scope {
      val Some(formulaText) = scraper findFormulaText doc
      formulaText must_== bigFormulaText
    }

  }

}
