package edu.gemini.aspen.gds.keywords.database

import org.scalatest.junit.AssertionsForJUnit
import edu.gemini.aspen.gds.api.CollectedValue
import org.junit.{Before, Test}
import edu.gemini.aspen.gds.api.Conversions._
import edu.gemini.fits.{DefaultHeader, Header, HeaderItem}
import collection.JavaConversions._

class KeywordsDatabaseTest extends AssertionsForJUnit {
  var db: KeywordsDatabaseImpl = null
  val key = "keyword"
  val key2 = "keyword2"
  val value: AnyRef = (0.1).asInstanceOf[AnyRef]
  val colVal = new CollectedValue(key, value, "my comment", 0)
  val colVal2 = new CollectedValue(key2, value, "my comment", 1)
  val dataLabel = "GS-2011B"
  val dataLabel2 = "GS-2011A"
  val headerItem:HeaderItem = colVal //implicit conversion
  val headerItem2:HeaderItem = colVal2 //implicit conversion

  @Before
  def setup() {
    db = new KeywordsDatabaseImpl
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
    db ! Store(dataLabel, colVal)
    db ! Store(dataLabel, colVal2)

    val ret = db !? (1000,Retrieve(dataLabel, key))
    assert(!ret.isEmpty)//we didn't get a timeout
    val opt = ret.get.asInstanceOf[Option[HeaderItem]]
    assert(opt.isDefined)//the value was at the DB
    assert(opt.get == headerItem)//the value was correct

    val ret2 = db !? (1000,Retrieve(dataLabel, key2))
    assert(!ret2.isEmpty)//we didn't get a timeout
    val opt2 = ret2.get.asInstanceOf[Option[HeaderItem]]
    assert(opt2.isDefined)//the value was at the DB
    assert(opt2.get == headerItem2)//the value was correct
  }

  @Test
  def retrieveExtension(){
    db ! Store(dataLabel, colVal)
    db ! Store(dataLabel, colVal2)

    val ret = db !? (1000,RetrieveExtension(dataLabel, colVal.index))
    assert(!ret.isEmpty)//we didn't get a timeout
    val opt = ret.get.asInstanceOf[Option[Header]]
    assert(opt.isDefined)//the value was at the DB
    assert(opt.get.size==1)
    assert(opt.get.get(stringToFitsKeyword(key)) == headerItem)//the value was correct

  }


  @Test
  def multipleDatasets(){
    db ! Store(dataLabel, colVal)
    db ! Store(dataLabel2, colVal2)

    val ret = db !? (1000,Retrieve(dataLabel, key))
    assert(!ret.isEmpty)//we didn't get a timeout
    val opt = ret.get.asInstanceOf[Option[HeaderItem]]
    assert(opt.isDefined)//the value was at the DB
    assert(opt.get == headerItem)//the value was correct

    val ret2 = db !? (1000,Retrieve(dataLabel2, key2))
    assert(!ret2.isEmpty)//we didn't get a timeout
    val opt2 = ret2.get.asInstanceOf[Option[HeaderItem]]
    assert(opt2.isDefined)//the value was at the DB
    assert(opt2.get == headerItem2)//the value was correct
  }

}