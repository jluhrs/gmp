package edu.gemini.aspen.gds.seqexec


import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test
import edu.gemini.aspen.gds.api.Conversions._
import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.giapi.data.FitsKeyword
import edu.gemini.aspen.gds.staticheaderreceiver.{Store, TemporarySeqexecKeywordsDatabaseImpl}

class SeqexecActorsTest extends AssertionsForJUnit {

  @Test
  def testActor() {
    val db = new TemporarySeqexecKeywordsDatabaseImpl
    db.channel ! Store("label", "key", 1.asInstanceOf[AnyRef])

    val seqActor = new SeqexecActor(db, "label", new GDSConfiguration("GPI", "OBS_START_EVENT", "KEY", 0, "INT", true, "null", "SEQEXEC", "KEY", "0", "my comment"))

    assert(seqActor.collectValues == List(CollectedValue("KEY", 1.asInstanceOf[AnyRef], "my comment", 0)))
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
    db.channel ! Store("label", "TEST", 1.asInstanceOf[AnyRef])
    val factory = new SeqexecActorsFactory(db)
    factory.configure(List(GDSConfiguration(Instrument("GPI"), GDSEvent("OBS_PREP"), new FitsKeyword("TEST"), HeaderIndex(0), DataType("DOUBLE"), Mandatory(false), NullValue("NONE"), Subsystem("SEQEXEC"), Channel("ws:massAirmass"), ArrayIndex("NULL"), FitsComment("my comment"))))
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