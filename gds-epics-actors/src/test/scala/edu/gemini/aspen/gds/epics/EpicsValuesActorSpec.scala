package edu.gemini.aspen.gds.epics

import org.junit.Assert._
import org.junit.Test
import edu.gemini.epics.EpicsReader
import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}
import org.specs2.mock.Mockito
import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.gds.api.Conversions._

class EpicsValuesActorSpec extends Mockito {
    val dataLabel = new DataLabel("GS-2011")
    val epicsReader = mock[EpicsReader]

    val channelName = "ws:massAirmass"
    val referenceValue = Array[String]("an epics string", "another epics string")
    val fitsKeyword = new FitsKeyword("AIRMASS")
    val nullValue = DefaultValue("NONE")

    def buildConfiguration(mandatory: Boolean, arrayIndex: Int) =
        GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "STRING", mandatory, "NONE", "EPICS", channelName, arrayIndex, "Mean airmass for the observation")

    @Test
    def testReplyToCollect() {
        val configuration = buildConfiguration(true, 0)
        val epicsValueActor = new EpicsValuesActor(epicsReader, configuration)

        // mock return value
        epicsReader.getValue(channelName) returns referenceValue

        // Send an init message
        val result = epicsValueActor !! Collect

        result() match {
            case CollectedValue(keyword, value, comment, 0) :: Nil => {
                assertEquals(fitsKeyword, keyword)
                assertEquals(referenceValue(0), value)
                assertEquals("Mean airmass for the observation", comment)
            }
            case _ => fail("Should not reply other message ")
        }

        // verify mock
        there was one(epicsReader).getValue(channelName)
    }

    @Test
    def testAccessArrayElement() {
        val configuration = buildConfiguration(true, 1)
        val epicsValueActor = new EpicsValuesActor(epicsReader, configuration)

        // mock return value
        epicsReader.getValue(channelName) returns referenceValue

        // Send an init message
        val result = epicsValueActor !! Collect

        result() match {
            case CollectedValue(keyword, value, comment, 0) :: Nil
            => {
                assertEquals(fitsKeyword, keyword)
                assertEquals(referenceValue(1), value)
                assertEquals("Mean airmass for the observation", comment)
            }
            case _ => fail("Should not reply other message ")
        }

        // verify mock
        there was one(epicsReader).getValue(channelName)
    }

    // should provide a default value if the current one cannot be read
    @Test
    def testCollectingADefaultValue {
        val configuration = buildConfiguration(false, 0)
        val epicsValueActor = new EpicsValuesActor(epicsReader, configuration)

        // mock return value cannot be read
        epicsReader.getValue(channelName) returns null

        // Send an init message
        val result = epicsValueActor !! Collect


        result() match {
            case CollectedValue(keyword, value, comment, 0) :: Nil => {
                assertEquals(fitsKeyword, keyword)
                assertEquals(nullValue.value, value)
                assertEquals("Mean airmass for the observation", comment)
            }
            case _ => fail("Should not reply other message ")
        }

        // verify mock
        there was one(epicsReader).getValue(channelName)
    }

    @Test
    def testCollectError {
        val configuration = buildConfiguration(true, 0)
        val epicsValueActor = new EpicsValuesActor(epicsReader, configuration)

        // mock return value cannot be read
        epicsReader.getValue(channelName) returns null

        // Send an init message
        val result = epicsValueActor !! Collect

        result() match {
            case ErrorCollectedValue(keyword, error, comment, 0) :: Nil => {
                assertEquals(fitsKeyword, keyword)
                assertEquals(CollectionError.MandatoryRequired, error)
                assertEquals("Mean airmass for the observation", comment)
            }
            case _ => fail("Should not reply other message ")
        }

        // verify mock
        there was one(epicsReader).getValue(channelName)
    }

    @Test
    def testCollectTypeMismatch {
        val configuration = buildConfiguration(true, 0)
        val epicsValueActor = new EpicsValuesActor(epicsReader, configuration)

        // mock return value cannot be read
        epicsReader.getValue(channelName) returns Double.box(1.1)

        // Send an init message
        val result = epicsValueActor !! Collect

        result() match {
            case ErrorCollectedValue(keyword, error, comment, 0) :: Nil => {
                assertEquals(fitsKeyword, keyword)
                assertEquals(CollectionError.TypeMismatch, error)
                assertEquals("Mean airmass for the observation", comment)
            }
            case _ => fail("Should not reply other message ")
        }

        // verify mock
        there was one(epicsReader).getValue(channelName)
    }

    @Test
    def testArrayIndexOutOfBounds {
        val configuration = buildConfiguration(true, 2)
        val epicsValueActor = new EpicsValuesActor(epicsReader, configuration)

        // mock return value
        epicsReader.getValue(channelName) returns referenceValue

        // Send an init message
        val result = epicsValueActor !! Collect

        result() match {
            case ErrorCollectedValue(keyword, error, comment, 0) :: Nil
            => {
                assertEquals(fitsKeyword, keyword)
                assertEquals(CollectionError.ArrayIndexOutOfBounds, error)
                assertEquals("Mean airmass for the observation", comment)
            }
            case _ => fail("Should not reply other message ")
        }

        // verify mock
        there was one(epicsReader).getValue(channelName)
    }

}

