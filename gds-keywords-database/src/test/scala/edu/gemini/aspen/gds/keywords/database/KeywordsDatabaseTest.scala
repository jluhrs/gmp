package edu.gemini.aspen.gds.keywords.database

import org.scalatest.junit.AssertionsForJUnit
import edu.gemini.aspen.gds.api.CollectedValue
import org.junit.{Before, Test}
import edu.gemini.aspen.gds.api.Conversions._
import edu.gemini.fits.{DefaultHeader, Header, HeaderItem}
import edu.gemini.aspen.giapi.data.DataLabel

class KeywordsDatabaseTest extends AssertionsForJUnit {
  var db: KeywordsDatabaseImpl = null
  val key = "keyword"
  val key2 = "keyword2"
  val value = 0.1
  val colVal = new CollectedValue(key, value, "my comment", 0)
  val colVal2 = new CollectedValue(key2, value, "my comment", 1)
  val dataLabel = "GS-2011B"
  val dataLabel2 = "GS-2011A"
  val headerItem: HeaderItem = colVal //implicit conversion
  val headerItem2: HeaderItem = colVal2 //implicit conversion

  @Before
  def setup() {
    db = new KeywordsDatabaseImpl
  }

  def check(label: DataLabel, item: HeaderItem) {
    val list = List(new DefaultHeader(List(item)))
    val ret = db !? (1000, Retrieve(label))
    assert(!ret.isEmpty) //we didn't get a timeout
    val storedOpt = ret.get.asInstanceOf[Option[List[Header]]]
    assert(storedOpt.isDefined) //the set was at the DB
    for (storedList <- storedOpt; e1 <- storedList; e2 <- list) {
      assert(e1 == e2)
    }
  }

  @Test
  def testRetrieveAll() {

    db ! Store(dataLabel, colVal)
    check(dataLabel, headerItem)

  }


  @Test
  def retrieveEmpty() {
    val ret = db !? (1000, Retrieve(dataLabel))
    assert(!ret.isEmpty)
    val opt = ret.get.asInstanceOf[Option[List[Header]]]
    assert(opt.isEmpty)
  }

  @Test
  def retrieveWrongDataLabel() {
    db ! Store(dataLabel, colVal)
    val ret = db !? (1000, Retrieve("wrong"))
    assert(!ret.isEmpty)
    val opt = ret.get.asInstanceOf[Option[List[Header]]]
    assert(opt.isEmpty)
  }


  @Test
  def multipleDatasets() {
    db ! Store(dataLabel, colVal)
    db ! Store(dataLabel2, colVal2)

    check(dataLabel, headerItem)

    check(dataLabel2, headerItem2)
  }

  @Test
  def testClean() {
    val ret = db !? (1000, Retrieve(dataLabel))
    db ! Clean(dataLabel)
    retrieveEmpty()
  }
}