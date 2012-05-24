package edu.gemini.aspen.gds.properties

import org.junit.Test
import org.junit.Assert._
import edu.gemini.aspen.gds.api.Conversions._
import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.giapi.data.ObservationEvent

class PropertiesActorsTest {

  /**
   * Test for the common case of a value found in the local DB
   */
  /*@Test
  def testActor() {
    val seqActorInt = new SeqexecActor(db, "labelint",
      GDSConfiguration("GPI", "OBS_START_ACQ", "KEY", 0, "INT", true, "null", "SEQEXEC", "KEY", 0, "my comment") ::
        GDSConfiguration("GPI", "OBS_START_ACQ", "KEY", 0, "STRING", true, "null", "SEQEXEC", "KEY", 0, "my comment") ::
        GDSConfiguration("GPI", "OBS_START_ACQ", "KEY", 0, "DOUBLE", true, "null", "SEQEXEC", "KEY", 0, "my comment") ::
        Nil)
    assertEquals(
      List(CollectedValue("KEY", 1, "my comment", 0), CollectedValue("KEY", "1", "my comment", 0), CollectedValue("KEY", 1.0, "my comment", 0)),
      seqActorInt.collectValues)
  }

  @Test
  def testWrongType() {
    val db = new TemporarySeqexecKeywordsDatabaseImpl
    db ! Store("label", "KEY", "1.1")

    val seqActor = new SeqexecActor(db, "label", List(GDSConfiguration("GPI", "OBS_START_ACQ", "KEY", 0, "DOUBLE", true, "null", "SEQEXEC", "KEY", 0, "my comment")))
    assertEquals(List(ErrorCollectedValue("KEY", CollectionError.TypeMismatch, "my comment", 0)), seqActor.collectValues)
  }

  @Test
  def testActorInt() {
    db ! Store("label", "KEY", 1.asInstanceOf[AnyRef])

    val seqActor = new SeqexecActor(db, "label", GDSConfiguration("GPI", "OBS_START_ACQ", "KEY", 0, "INT", true, "null", "SEQEXEC", "KEY", 0, "my comment") :: Nil)

    assertEquals(List(CollectedValue("KEY", 1, "my comment", 0)), seqActor.collectValues)
  }

  /**
   * Test for a non mandatory value not found in the local DB
   */
  @Test
  def testNotMandatoryNotFoundValue() {
    val seqActor = new SeqexecActor(db, "label", GDSConfiguration("GPI", "OBS_START_ACQ", "KEY", 0, "INT", false, "DEFAULT", "SEQEXEC", "KEY", 0, "my comment") :: Nil)

    // should not return anything if the value cannot be read. The default will be added by an ErrorPolicy
    // it doesn't matter at this point if the item is mandatory or not
    assertEquals(Nil, seqActor.collectValues)
  }

  /**
   * Test for a mandatory value not found in the local DB
   */
  @Test
  def testMandatoryNotFoundValue() {
    val seqActor = new SeqexecActor(db, "label", GDSConfiguration("GPI", "OBS_START_ACQ", "KEY", 0, "INT", true, "DEFAULT", "SEQEXEC", "KEY", 0, "my comment") :: Nil)

    // should not return anything if the value cannot be read. The default will be added by an ErrorPolicy
    // it doesn't matter at this point if the item is mandatory or not
    assertEquals(Nil, seqActor.collectValues)
  }

  @Test
  def testActorFactory() {
    db ! Store("label", "TEST", (1.0).asInstanceOf[AnyRef])

    val factory = new SeqexecActorsFactory(db)
    factory.configure(List(GDSConfiguration("GPI", "OBS_PREP", "TEST", 0, "DOUBLE", false, "NONE", "SEQEXEC", "ws:massAirmass", 0, "my comment")))
    assertTrue(factory.buildActors(ObservationEvent.OBS_START_ACQ, "label").head.collectValues().isEmpty)
    assertTrue(factory.buildActors(ObservationEvent.OBS_END_ACQ, "label").head.collectValues().isEmpty)

    val actors = factory.buildActors(ObservationEvent.OBS_PREP, "label")
    assertEquals(1, actors.length)

    val values = actors.head.collectValues()
    values.head match {
      case CollectedValue(fits, value, comment, 0) => {
        assertEquals(stringToFitsKeyword("TEST"), fits)
        assertEquals(1.0, value)
        assertEquals("my comment", comment)
      }
      case _ => fail("Wrong answer")
    }
    assertEquals(1, values.length)

  }*/
}