package edu.gemini.aspen.gds.staticheaderreceiver


import org.scalatest.junit.AssertionsForJUnit
import edu.gemini.aspen.gds.api.Conversions._
import org.junit.{Before, Test}
import edu.gemini.aspen.gds.keywords.database.{RetrieveProgramId, ProgramIdDatabaseImpl, ProgramIdDatabase}
import scala.Some

class HeaderReceiverTest extends AssertionsForJUnit {
  var db: TemporarySeqexecKeywordsDatabase = _
  var ch: actors.Channel[SeqexecDbMsg] = _
  var pdb: ProgramIdDatabase = _

  @Before
  def setup() {
    db = new TemporarySeqexecKeywordsDatabaseImpl
    ch = db.channel
    pdb = new ProgramIdDatabaseImpl
    RequestHandler.setDatabases(db, pdb)
    RequestHandler.start()
  }

  @Test
  def testDB() {
    ch ! Store("label", "key", 1.asInstanceOf[AnyRef])
    (ch !? (1000, Retrieve("label", "key"))) match {
      case Some(Some(1)) =>
      case _ => fail()
    }
    (ch !? (1000, Retrieve("wronglabel", "key"))) match {
      case Some(None) =>
      case _ => fail()
    }
    (ch !? (1000, Retrieve("label", "wrongkey"))) match {
      case Some(None) =>
      case _ => fail()
    }
    ch ! Clean("label")
    (ch !? (1000, Retrieve("label", "key"))) match {
      case Some(None) =>
      case _ => fail()
    }
    ch ! Clean("wronglabel")
  }

  @Test
  def testRequestHandler() {
    RequestHandler ! StoreKeyword("label", "key", 1.asInstanceOf[AnyRef])
    Thread.sleep(100) //allow for messages to arrive
    (ch !? (1000, Retrieve("label", "key"))) match {
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
    xml.initObservation("id", "label")
    Thread.sleep(100) //allow for messages to arrive
    (ch !? (1000, Retrieve("label", "key"))) match {
      case Some(Some(1)) =>
      case _ => fail()
    }
    (pdb !? (1000, RetrieveProgramId("label"))) match {
      case Some(Some("id")) =>
      case _ => fail()
    }
  }

}