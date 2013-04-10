package com.nowanswers.spider

import com.nowanswers.mineralogy.Mineral
import com.nowanswers.chemistry.BasicFormulaParserComponent
import com.novus.salat.annotations.raw.Key

trait MineralStoreComponent {

  def store: MineralStore

  trait MineralStore {
    def storeMineral(mineral: Mineral)
  }
}


case class MineralDTO(@Key("_id") name: String, formula: String)
object MineralDTO {
  def apply(mineral: Mineral): MineralDTO = MineralDTO(mineral.name, mineral.formula.toString() )
}

trait MongoMineralStoreComponent extends MineralStoreComponent with BasicFormulaParserComponent {

  import com.novus.salat._
  import com.novus.salat.global._
  import com.mongodb.casbah.Imports._
  import com.mongodb.casbah.MongoCollection


  def collection : MongoCollection

  val store = new MineralStore {

    def reconstitute(dto: MineralDTO) = {
      parser.parseFormula(dto.formula) match {
        case Right(formula) => Mineral(dto.name, formula)
        case _ => throw new RuntimeException("Couldn't parse formula from database, name = %s, formula = %s".format(dto.name, dto.formula))
      }

    }

    def storeMineral (mineral : Mineral) {
      val dto = MineralDTO(mineral)
      val mob = grater[MineralDTO].asDBObject(dto)
      collection insert mob

    }

    def loadMineral (name : String) : Option[Mineral] = {
      collection findOne (MongoDBObject ("name" -> name)) map {
        grater[MineralDTO].asObject(_)
      } map { reconstitute(_) }
    }

  }
}
