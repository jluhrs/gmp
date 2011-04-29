package edu.gemini.aspen.gds.keywords.database

import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test
import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}
import edu.gemini.aspen.gds.api.CollectedValue
import collection.mutable.{HashSet, HashMap}


class KeywordsDatabaseTest extends AssertionsForJUnit {
  @Test
  def testBasic() {
    val db:KeywordsDatabase = new KeywordsDatabaseImpl
    val key= new FitsKeyword("gpi:keyword")
    val value:AnyRef = (0.1).asInstanceOf[AnyRef]
    val colVal = new CollectedValue(key,value,"my comment")

    db.store(new DataLabel("GS-2011B"),colVal)

    assert(db.retrieve(new DataLabel("GS-2011B"),key).isDefined)

    assert(colVal == db.retrieve(new DataLabel("GS-2011B"),key).get)
  }

  @Test
  def testRetrieveAll() {
    val db:KeywordsDatabase = new KeywordsDatabaseImpl
    val key= new FitsKeyword("gpi:keyword")
    val value:AnyRef = (0.1).asInstanceOf[AnyRef]
    val colVal = new CollectedValue(key,value,"my comment")

    db.store(new DataLabel("GS-2011B"),colVal)

    assert(db.retrieveAll(new DataLabel("GS-2011B")).isDefined)

    val set = new HashSet[CollectedValue]()
    set.add(colVal)
    val storedSet = db.retrieveAll(new DataLabel("GS-2011B")).get
    assert(storedSet.equals(set))
  }
}