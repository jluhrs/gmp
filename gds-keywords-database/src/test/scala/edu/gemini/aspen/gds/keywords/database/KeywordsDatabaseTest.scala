package edu.gemini.aspen.gds.keywords.database

import org.scalatest.junit.AssertionsForJUnit
import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}
import edu.gemini.aspen.gds.api.CollectedValue
import collection.mutable.HashSet
import org.junit.{Before, Test}

class KeywordsDatabaseTest extends AssertionsForJUnit {
  var db: KeywordsDatabaseImpl = null
  val key = new FitsKeyword("gpi:keyword")
  val value: AnyRef = (0.1).asInstanceOf[AnyRef]
  val colVal = new CollectedValue(key, value, "my comment")
  val dataLabel = new DataLabel("GS-2011B")

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
    val opt = ret.get.asInstanceOf[Option[CollectedValue]]
    assert(opt.isDefined)//the value was at the DB
    assert(opt.get == colVal)//the value was correct
  }

  @Test
  def testRetrieveAll() {
    val set = new HashSet[CollectedValue]()
    set.add(colVal)

    db ! Store(dataLabel, colVal)

    val ret = db !? (1000,RetrieveAll(dataLabel))
    assert(ret!=None)//we didn't get a timeout
    val storedSet = ret.get.asInstanceOf[Option[Set[CollectedValue]]]
    assert(storedSet.isDefined)//the set was at the DB
    assert(storedSet.get == set)//the set was correct

  }

}