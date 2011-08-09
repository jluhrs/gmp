package edu.gemini.aspen.gds.staticheaderreceiver


import org.scalatest.junit.AssertionsForJUnit
import edu.gemini.aspen.gds.api.Conversions._
import org.junit.{Before, Test}
import edu.gemini.aspen.gds.keywords.database.impl.ProgramIdDatabaseImpl
import edu.gemini.aspen.gds.keywords.database.{RetrieveProgramId, ProgramIdDatabase}
import edu.gemini.aspen.gds.staticheaderreceiver.TemporarySeqexecKeywordsDatabaseImpl.{Store, Retrieve, Clean}
import scala.Some

class HeaderReceiverTest extends AssertionsForJUnit {
  var db: TemporarySeqexecKeywordsDatabase = _
  var pdb: ProgramIdDatabase = _

  @Before
  def setup() {
    db = new TemporarySeqexecKeywordsDatabaseImpl
    pdb = new ProgramIdDatabaseImpl
    RequestHandler.setDatabases(db, pdb)
    RequestHandler.start()
  }

  @Test
  def testDB() {
    db ! Store("label", "key", 1.asInstanceOf[AnyRef])
    (db !? (1000, Retrieve("label", "key"))) match {
      case Some(Some(1)) =>
      case _ => fail()
    }
    (db !? (1000, Retrieve("wronglabel", "key"))) match {
      case Some(None) =>
      case _ => fail()
    }
    (db !? (1000, Retrieve("label", "wrongkey"))) match {
      case Some(None) =>
      case _ => fail()
    }
    db ! Clean("label")
    (db !? (1000, Retrieve("label", "key"))) match {
      case Some(None) =>
      case _ => fail()
    }
    db ! Clean("wronglabel")
  }

  @Test
  def testRequestHandler() {
    RequestHandler ! StoreKeyword("label", "key", 1.asInstanceOf[AnyRef])
    Thread.sleep(100) //allow for messages to arrive
    (db !? (1000, Retrieve("label", "key"))) match {
      case Some(Some(1)) =>
      case _ => fail()
    }

    RequestHandler ! InitObservation("programId", "label")
    Thread.sleep(100)
    (pdb !? (1000, RetrieveProgramId("label"))) match {
      case Some(Some("programId")) =>
      case _ => fail()
    }

    //todo: test wrong message handling???
    //RequestHandler ! "wrong message"

  }

  @Test
  def testXmlRpcReceiver() {
    val xml = new XmlRpcReceiver
    xml.storeKeyword("label", "key", 1)
    xml.storeKeywords("label2", ("key,INT,1" :: "key2,DOUBLE,1.0" :: "key3,STRING,uno" :: Nil).toArray)
    xml.initObservation("id", "label")
    Thread.sleep(100) //allow for messages to arrive
    (db !? (1000, Retrieve("label", "key"))) match {
      case Some(Some(1)) =>
      case _ => fail()
    }
    (db !? (1000, Retrieve("label2", "key"))) match {
      case Some(Some(1)) =>
      case _ => fail()
    }
    (db !? (1000, Retrieve("label2", "key2"))) match {
      case Some(Some(1.0)) =>
      case _ => fail()
    }
    (db !? (1000, Retrieve("label2", "key3"))) match {
      case Some(Some("uno")) =>
      case _ => fail()
    }
    (pdb !? (1000, RetrieveProgramId("label"))) match {
      case Some(Some("id")) =>
      case _ => fail()
    }
  }

}