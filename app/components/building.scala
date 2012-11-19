package com.nowanswers.spider

import com.nowanswers.chemistry.FormulaParserComponent
import spider.mineral.MineralPageScraperComponent
import xml.Elem
import com.nowanswers.mineralogy.Mineral

trait MineralBuilderComponent {

  def builder: MineralBuilder

  trait MineralBuilder {

    def buildMineral(doc: Elem): Option[Mineral]

  }

}


trait RealMineralBuilderComponent extends MineralBuilderComponent {

  self: FormulaParserComponent with MineralPageScraperComponent =>

  val builder = new MineralBuilder {

    def buildMineral(doc: xml.Elem): Option[Mineral] = {
      for (
        formulaText <- scraper.findFormulaText(doc);
        mineralName <- scraper.findTitle(doc)
      ) yield
        parser parseFormula formulaText match {
          case Right(formula) => Mineral(mineralName, formula)
          case Left(message) => throw new RuntimeException("unable to parse %s into a formula; error %s".format(formulaText, message))
        }
    }

  }

}