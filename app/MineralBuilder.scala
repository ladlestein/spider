package spider.mineral

import spider.LinkFinderComponent
import xml.{Node, NodeSeq, Elem, Document}
import com.nowanswers.chemistry._
import com.nowanswers.chemistry.Formula
import com.nowanswers.chemistry.ElementalTerm
import com.nowanswers.chemistry.Element
import com.nowanswers.chemistry.QuantifiedTerm
import scala.xml

/**
 * Created with IntelliJ IDEA.
 * User: ladlestein
 * Date: 7/9/12
 * Time: 6:30 PM
 * To change this template use File | Settings | File Templates.
 */

trait MineralBuilderComponent {

  val builder: MineralBuilder

  trait MineralBuilder {

    def buildMineral(doc: Elem): Option[Mineral]

  }

}


trait RealMineralBuilderComponent extends MineralBuilderComponent {

  self: FormulaParserComponent =>
   val builder = new MineralBuilder {

     def buildMineral(doc: xml.Elem): Option[Mineral] = {
       for (
         formulaText <- findFormulaText(doc);
         mineralName <- findTitle(doc)
       ) yield
         parser parseFormula formulaText match {
           case Right(formula) => Mineral(mineralName, formula)
           case Left(message) => throw new RuntimeException("unable to parse %s into a formula; error %s".format(formulaText, message))
         }
     }

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

     def findByAttribute (seq : NodeSeq, name : String, value : String) : Option[ Node ] = {
       seq.find {
         node => {
           val attribute = node.attribute (name)
           val attributeValue = attribute map {
             x => x head
           } map {
             x => x.text
           }
           attributeValue == Some (value)
         }
       }
     }

   }


}

