package edu.gemini.aspen.gds.keywords.database

import org.scalatest.junit.AssertionsForJUnit
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.api.CollectedValue
import collection.mutable.HashSet
import org.junit.{Before, Test}
import edu.gemini.fits.HeaderItem
import edu.gemini.aspen.gds.api.Conversions._

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
    assert(ret!=None)//we didn't get a timeout
    val opt = ret.get.asInstanceOf[Option[HeaderItem]]
    assert(opt.isDefined)//the value was at the DB
    assert(opt.get == headerItem)//the value was correct
  }

  @Test
  def testRetrieveAll() {
    val set = new HashSet[HeaderItem]()
    set.add(headerItem)

    db ! Store(dataLabel, colVal)

    val ret = db !? (1000,RetrieveAll(dataLabel))
    assert(ret!=None)//we didn't get a timeout
    val storedSet = ret.get.asInstanceOf[Option[Set[HeaderItem]]]
    assert(storedSet.isDefined)//the set was at the DB
    for(e1 <- storedSet.get;e2 <- set){
      assert(e1 == e2)
    }
  }

}