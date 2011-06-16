package edu.gemini.aspen.gds.seqexec


import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test
import edu.gemini.aspen.gds.api.Conversions._
import edu.gemini.aspen.gds.staticheaderreceiver.TemporarySeqexecKeywordsDatabaseImpl
import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.giapi.data.FitsKeyword
import edu.gemini.aspen.gds.staticheaderreceiver.TemporarySeqexecKeywordsDatabaseImpl.Store

class SeqexecActorsTest extends AssertionsForJUnit {

  @Test
  def testActor() {
    val db = new TemporarySeqexecKeywordsDatabaseImpl
    db ! Store("labelint", "key", 1.asInstanceOf[AnyRef])
    db ! Store("labelstring", "key", "1".asInstanceOf[AnyRef])
    db ! Store("labeldouble", "key", 1.0.asInstanceOf[AnyRef])

    val seqActorInt = new SeqexecActor(db, "labelint", new GDSConfiguration("GPI", "OBS_START_EVENT", "KEY", 0, "INT", true, "null", "SEQEXEC", "KEY", "0", "my comment"))
    assert(seqActorInt.collectValues == List(CollectedValue("KEY", 1, "my comment", 0)))
    val seqActorString = new SeqexecActor(db, "labelstring", new GDSConfiguration("GPI", "OBS_START_EVENT", "KEY", 0, "STRING", true, "null", "SEQEXEC", "KEY", "0", "my comment"))
    assert(seqActorString.collectValues == List(CollectedValue("KEY", "1", "my comment", 0)))
    val seqActorDouble = new SeqexecActor(db, "labeldouble", new GDSConfiguration("GPI", "OBS_START_EVENT", "KEY", 0, "DOUBLE", true, "null", "SEQEXEC", "KEY", "0", "my comment"))
    assert(seqActorDouble.collectValues == List(CollectedValue("KEY", 1.0, "my comment", 0)))
  }

  @Test
  def testWrongType() {
    val db = new TemporarySeqexecKeywordsDatabaseImpl
    db ! Store("label", "key", 1.1.asInstanceOf[AnyRef])

    val seqActor = new SeqexecActor(db, "label", new GDSConfiguration("GPI", "OBS_START_EVENT", "KEY", 0, "INT", true, "null", "SEQEXEC", "KEY", "0", "my comment"))
    assert(seqActor.collectValues == Nil)
  }

  @Test
  def testEmpty() {
    val db = new TemporarySeqexecKeywordsDatabaseImpl

    val seqActor = new SeqexecActor(db, "label", new GDSConfiguration("GPI", "OBS_START_EVENT", "KEY", 0, "INT", true, "null", "SEQEXEC", "KEY", "0", "my comment"))

    assert(seqActor.collectValues == List())
  }

  @Test
  def testActorFactory() {
    val db = new TemporarySeqexecKeywordsDatabaseImpl
    db ! Store("label", "TEST", (1.0).asInstanceOf[AnyRef])
    val factory = new SeqexecActorsFactory(db)
    factory.configure(List(GDSConfiguration(Instrument("GPI"), GDSEvent("OBS_PREP"), new FitsKeyword("TEST"), HeaderIndex(0), DataType("DOUBLE"), Mandatory(false), DefaultValue("NONE"), Subsystem("SEQEXEC"), Channel("ws:massAirmass"), ArrayIndex("NULL"), FitsComment("my comment"))))
    assert(factory.buildStartAcquisitionActors("label").isEmpty)
    assert(factory.buildEndAcquisitionActors("label").isEmpty)
    val actors = factory.buildPrepareObservationActors("label")
    assert(actors.length == 1)
    val values = actors.head.collectValues()
    values.head match {
      case CollectedValue(fits, value, comment, 0) => {
        assert(fits == stringToFitsKeyword("TEST"))
        assert(value == 1)
        assert(comment == "my comment")
      }
      case _ => fail("Wrong answer")
    }
  }
}