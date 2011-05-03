package edu.gemini.aspen.gds.keywords.database

import org.scalatest.junit.AssertionsForJUnit
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.api.CollectedValue
import collection.mutable.HashSet
import org.junit.{Before, Test}
import edu.gemini.aspen.gds.api.Conversions._
import edu.gemini.fits.{DefaultHeader, Header, HeaderItem}
import collection.JavaConversions._

class KeywordsDatabaseTest extends AssertionsForJUnit {
  var db: KeywordsDatabaseImpl = null
  val key = "keyword"
  val value: AnyRef = (0.1).asInstanceOf[AnyRef]
  val colVal = new CollectedValue(key, value, "my comment", 0)
  val dataLabel = "GS-2011B"
  val headerItem:HeaderItem = colVal //implicit conversion

  @Before
  def setup() {
    db = new KeywordsDatabaseImpl
    db.start()
  }

  @Test
  def testBasic() {


    db ! Store(dataLabel, colVal)

    val ret = db !? (1000,Retrieve(dataLabel, key))
    assert(!ret.isEmpty)//we didn't get a timeout
    val opt = ret.get.asInstanceOf[Option[HeaderItem]]
    assert(opt.isDefined)//the value was at the DB
    assert(opt.get == headerItem)//the value was correct
  }

  @Test
  def testRetrieveAll() {
    val list = List(new DefaultHeader(List(headerItem)))

    db ! Store(dataLabel, colVal)

    val ret = db !? (1000,RetrieveAll(dataLabel))
    assert(!ret.isEmpty)//we didn't get a timeout
    val storedOpt = ret.get.asInstanceOf[Option[List[Header]]]
    assert(storedOpt.isDefined)//the set was at the DB
    for(storedList <- storedOpt;e1 <- storedList;e2 <- list){
      assert(e1 == e2)
    }
  }

  @Test
  def retrieveEmpty() {
    val ret = db !? (1000, Retrieve(dataLabel, key))
    assert(!ret.isEmpty)
    val opt = ret.get.asInstanceOf[Option[HeaderItem]]
    assert(opt.isEmpty)
  }

  @Test
  def retrieveWrongDataLabel() {
    db ! Store(dataLabel, colVal)
    val ret = db !? (1000, Retrieve("wrong", key))
    assert(!ret.isEmpty)
    val opt = ret.get.asInstanceOf[Option[HeaderItem]]
    assert(opt.isEmpty)
  }

  @Test
  def retrieveWrongKey() {
    db ! Store(dataLabel, colVal)
    val ret = db !? (1000, Retrieve(dataLabel, "wrong"))
    assert(!ret.isEmpty)
     val opt = ret.get.asInstanceOf[Option[HeaderItem]]
    assert(opt.isEmpty)
  }

  @Test
  def multipleHeaders(){
      //todo: test with two items with different index
  }

  @Test
  def multipleDatasets(){
    //todo: test with different datasets
  }

}