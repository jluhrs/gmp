package edu.gemini.aspen.gds.seqexec


import org.scalatest.junit.AssertionsForJUnit
import org.junit.{Test}
import edu.gemini.aspen.gds.api.Conversions._
import collection.JavaConversions._
import edu.gemini.aspen.gds.staticheaderreceiver.{StoreKeyword, TemporarySeqexecKeywordsDatabaseImpl}
import edu.gemini.aspen.gds.api._

class SeqexecActorsTest extends AssertionsForJUnit {

  @Test
  def testActor() {
    val db = new TemporarySeqexecKeywordsDatabaseImpl
    db ! StoreKeyword("label","key",1.asInstanceOf[AnyRef])

    val seqActor = new SeqexecActor(db,"label", new GDSConfiguration("GPI","OBS_START_EVENT","KEY",0,"INT",true,"null","SEQEXEC","KEY","0","my comment"))


    assert(seqActor.collectValues == List(CollectedValue("KEY",Some(1),"my comment",0)))
  }

  //todo: test actor factory
}