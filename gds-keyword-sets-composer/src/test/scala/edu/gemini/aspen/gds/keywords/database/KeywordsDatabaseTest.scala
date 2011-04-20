package edu.gemini.aspen.gds.keywords.database

import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test
import edu.gemini.aspen.giapi.data.Dataset
import collection.mutable.HashMap


class KeywordsDatabaseTest extends AssertionsForJUnit {
  @Test
  def testBasic() = {
    val db:KeywordsDatabase = new KeywordsDatabaseImpl


    val value:AnyRef = (0.1).asInstanceOf[AnyRef]

    db.store(new Dataset("GS-2011B"),"gpi:keyword",value)

    assert(db.retrieve(new Dataset("GS-2011B"),"gpi:keyword").isDefined)

    assert(value == db.retrieve(new Dataset("GS-2011B"),"gpi:keyword").get)
  }

  @Test
  def testRetrieveAll() = {
    val db:KeywordsDatabase = new KeywordsDatabaseImpl


    val value:AnyRef = (0.1).asInstanceOf[AnyRef]

    db.store(new Dataset("GS-2011B"),"gpi:keyword",value)

    assert(db.retrieveAll(new Dataset("GS-2011B")).isDefined)

    val map = new HashMap[String, AnyRef]()
    map.put("gpi:keyword",value)
    val storedMap = db.retrieveAll(new Dataset("GS-2011B")).get
    for(entry <- storedMap){
      assert(entry._2 == map.get(entry._1).orNull)
    }
  }
}