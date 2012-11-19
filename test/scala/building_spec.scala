package com.nowanswers.spider

import com.nowanswers.chemistry._
import com.nowanswers.chemistry.Formula
import com.nowanswers.chemistry.QuantifiedTerm
import com.nowanswers.chemistry.SubsitutionGroup
import org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import scala.Right
import scala.Some
import spider.mineral.MineralPageScraperComponent
import com.nowanswers.mineralogy.Mineral

class MineralBuilderSpec extends Specification with RealMineralBuilderComponent with MineralPageScraperComponent with FormulaParserComponent with Mockito {

  val parserFactory = new SAXFactoryImpl
  val testParser = parserFactory.newSAXParser
  val source = {
    val stream = getClass.getClassLoader.getResourceAsStream ("svyazhinite.html")
    scala.io.Source.fromInputStream (stream)
  }
  val loader = xml.XML.withSAXParser(testParser)
  val doc = loader.loadString(source.mkString)

  val parser = mock[FormulaParser]
  val scraper = mock[MineralPageScraper]

  val bigFormulaText = "(Mg,Mn2+,Ca)(Al,Fe3+)(SO4)2F·14H2O"

  "The mineral visitor" should {

    "build a mineral" in new Scope {
      import Element._
      val bigFormula = Formula(
        List(
          QuantifiedTerm(SubsitutionGroup(Mg, `Mn+2`, Ca)),
          QuantifiedTerm(SubsitutionGroup(Al, `Fe+3`)),
          QuantifiedTerm(S + (O*4), 2), F), 14)
      scraper.findTitle(doc) returns Option( "Svyazhinite" )
      scraper.findFormulaText(doc) returns Option( bigFormulaText )
      parser.parseFormula(bigFormulaText) returns Right(bigFormula)
      val Some(mineral) = builder buildMineral doc
      mineral must_== Mineral("Svyazhinite", bigFormula)
    }
  }

}
