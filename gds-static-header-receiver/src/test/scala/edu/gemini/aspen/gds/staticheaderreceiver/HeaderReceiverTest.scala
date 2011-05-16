package edu.gemini.aspen.gds.staticheaderreceiver


import org.scalatest.junit.AssertionsForJUnit
import edu.gemini.aspen.gds.api.Conversions._
import collection.JavaConversions._
import org.junit.{Before, Test}
import edu.gemini.aspen.gds.keywords.database.{RetrieveProgramId, ProgramIdDatabaseImpl, ProgramIdDatabase}

class HeaderReceiverTest extends AssertionsForJUnit {
  var db: TemporarySeqexecKeywordsDatabase = _
  var pdb: ProgramIdDatabase = _

  @Before
  def setup() {
    db = new TemporarySeqexecKeywordsDatabaseImpl
    pdb = new ProgramIdDatabaseImpl
    RequestHandler.setDatabases(db,pdb)
    RequestHandler.start()
  }

  @Test
  def testDB() {
    db ! StoreKeyword("label", "key", 1.asInstanceOf[AnyRef])
    assert(db !? RetrieveValue("label", "key") == Some(1))
    assert((db !? RetrieveValue("wronglabel", "key")).asInstanceOf[Option[AnyRef]].isEmpty)
    assert((db !? RetrieveValue("label", "wrongkey")).asInstanceOf[Option[AnyRef]].isEmpty)
    db ! Clean("label")
    assert((db !? RetrieveValue("label", "key")).asInstanceOf[Option[AnyRef]].isEmpty)
    db ! Clean("wronglabel")
  }

  @Test
  def testRequestHandler() {
    RequestHandler ! StoreKeyword("label", "key", 1.asInstanceOf[AnyRef])
    Thread.sleep(100) //allow for messages to arrive
    assert(db !? RetrieveValue("label", "key") == Some(1))

    RequestHandler ! InitObservation("programId", "label")
    Thread.sleep(100)
    assert(pdb !? RetrieveProgramId("label") == Some("programId"))

    //todo: test wrong message handling???
    //RequestHandler ! "wrong message"

  }


  @Test
  def testXmlRpcReceiver() {
    val xml = new XmlRpcReceiver
    xml.storeKeyword("label", "key", 1)
    xml.initObservation("id","label")
    Thread.sleep(100) //allow for messages to arrive
    assert(db !? RetrieveValue("label", "key") == Some(1))
    assert(pdb !? RetrieveProgramId("label") == Some("id"))
  }

}