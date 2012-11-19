import com.mongodb.casbah.Imports._
import com.novus.salat.annotations.raw.Key
import com.novus.salat.dao.SalatDAO
import com.nowanswers.chemistry._
import com.nowanswers.chemistry.Element._
import com.nowanswers.mineralogy.Mineral
import com.nowanswers.spider.MongoMineralStoreComponent
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class MongoMineralStoreSpec extends {

  private val connection = MongoConnection()
  private val database = connection("spider")
  val collection = database("minerals")

} with Specification with MongoMineralStoreComponent {

  "The mineral store" should {
    "store a simple mineral" in new Scope {

      pending
      val quartz = Mineral("Quartz", Si + ( O * 4 ))
      store storeMineral quartz

      ( store loadMineral "Quartz" ) must_== Some(quartz)
    }
  }

}

