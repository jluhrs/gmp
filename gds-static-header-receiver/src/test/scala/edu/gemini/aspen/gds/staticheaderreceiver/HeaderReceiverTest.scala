package edu.gemini.aspen.gds.staticheaderreceiver


import org.scalatest.junit.AssertionsForJUnit
import edu.gemini.aspen.gds.api.Conversions._
import collection.JavaConversions._
import org.junit.{Before, Test}

class HeaderReceiverTest extends AssertionsForJUnit {
  var db: TemporarySeqexecKeywordsDatabase = _

  @Before
  def setup() {
    db = new TemporarySeqexecKeywordsDatabaseImpl
    RequestHandler.setDatabase(db)
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

    RequestHandler ! InitObservation("programID", "label")
    //todo: test init observation

    //todo: test wrong message handling???
    //RequestHandler ! "wrong message"

  }


  @Test
  def testXmlRpcReceiver() {
    val xml = new XmlRpcReceiver
    xml.storeKeyword("label", "key", 1)
    Thread.sleep(100) //allow for messages to arrive
    assert(db !? RetrieveValue("label", "key") == Some(1))
  }

}