package edu.gemini.aspen.gds.staticheaderreceiver


import org.scalatest.junit.AssertionsForJUnit
import org.junit.{Test}
import edu.gemini.aspen.gds.api.Conversions._
import collection.JavaConversions._

class HeaderReceiverTest extends AssertionsForJUnit {

  @Test
  def testDB() {
    val db = new TemporarySeqexecKeywordsDatabaseImpl
    db ! StoreKeyword("label", "key", 1.asInstanceOf[AnyRef])
    assert(db !? RetrieveValue("label", "key") == Some(1))
    assert((db !? RetrieveValue("wronglabel", "key")).asInstanceOf[Option[AnyRef]].isEmpty)
    assert((db !? RetrieveValue("label", "wrongkey")).asInstanceOf[Option[AnyRef]].isEmpty)
    db ! Clean("label")
    assert((db !? RetrieveValue("label", "key")).asInstanceOf[Option[AnyRef]].isEmpty)
    db ! Clean("wronglabel")
  }

  //todo: test xmlrpc server
  //todo: test requesthandler

}