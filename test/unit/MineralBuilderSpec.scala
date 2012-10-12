package spider.mineral

import org.specs2.mutable._
import org.specs2.specification.Scope
import com.nowanswers.chemistry._
import org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl
import org.specs2.mock.Mockito

/**
 * Created with IntelliJ IDEA.
 * User: ladlestein
 * Date: 6/23/12
 * Time: 9:57 PM
 * To change this template use File | Settings | File Templates.
 */

class MineralBuilderSpec extends Specification with RealMineralBuilderComponent with FormulaParserComponent with Mockito {

  val parserFactory = new SAXFactoryImpl
  val testParser = parserFactory.newSAXParser
  val stream = scala.io.Source.fromInputStream (getClass.getClassLoader.getResourceAsStream ("svyazhinite.html"))
  val loader = xml.XML.withSAXParser(testParser)
  val doc = loader.loadString(stream.mkString)

  val parser = mock[FormulaParser]

  val bigFormulaText = "(Mg,Mn2+,Ca)(Al,Fe3+)(SO4)2F·14H2O"

  "The mineral visitor" should {

    "be able to find something" in new Scope {
      val xml = <stuff><guy size="medium"></guy><guy size="large"></guy></stuff>
      builder.findByAttribute(xml \\ "guy", "size", "large")
    }

    "find the title of the mineral" in new Scope {
      val Some(title) = builder findTitle doc
      title must_== "Svyazhinite"
    }

    "find the chemical formula text for the mineral" in new Scope {
      val Some(formulaText) = builder findFormulaText doc
      formulaText must_== bigFormulaText
    }

    "build a mineral" in new Scope {
      import Element._
      val bigFormula = Formula(
        List(
          QuantifiedTerm(SubsitutionGroup(Mg, Mn+2, Ca)),
          QuantifiedTerm(SubsitutionGroup(Al, Fe+3)),
          QuantifiedTerm(S ~ (O*4), 2), F), 14)
      parser.parseFormula(bigFormulaText) returns Right(bigFormula)
      val Some(mineral) = builder buildMineral doc
      mineral must_== Mineral("Svyazhinite", bigFormula)
    }
  }

}
