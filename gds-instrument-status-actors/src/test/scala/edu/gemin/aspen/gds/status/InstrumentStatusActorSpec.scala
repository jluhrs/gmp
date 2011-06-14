package edu.gemin.aspen.gds.status

import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Spec
import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}
import org.specs2.mock.Mockito
import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.giapi.status.StatusDatabaseService
import edu.gemini.aspen.giapi.status.impl.BasicStatus

@RunWith(classOf[JUnitRunner])
class InstrumentStatusActorSpec extends Spec with ShouldMatchers with Mockito {
    val defaultValue = "DEFAULT"
    val fitsKeyword = new FitsKeyword("GPISTATUS")
    val dataLabel = new DataLabel("GS-2011")
    val statusDB = mock[StatusDatabaseService]
    val statusItemName = "gpi:status1"

    describe("An InstrumentStatusActor") {
        it("should reply to Collect messages") {
            val configuration = buildConfiguration(statusItemName)

            val referenceValue = "ok"
            // mock return value
            val statusItem = new BasicStatus[String](statusItemName, referenceValue)
            statusDB.getStatusItem(statusItemName) returns statusItem

            val instrumentStatusActor = new InstrumentStatusActor(statusDB, configuration)

            // Send a Collect message and wait the response
            val result = instrumentStatusActor !! Collect

            result() match {
                case CollectedValue(keyword, value, comment, 0) :: Nil
                    => keyword should equal(fitsKeyword)
                    value should equal(referenceValue)
                    comment should be("Current global status of the instrument")
                case x: AnyRef => fail("Should not reply other message ")
            }

            // verify mock
            there was one(statusDB).getStatusItem(statusItemName)
        }
        it("should reply to Collect messages even when status value is unknown") {
            val configuration = buildConfiguration(statusItemName)

            // mock return value
            statusDB.getStatusItem(statusItemName) returns null

            val instrumentStatusActor = new InstrumentStatusActor(statusDB, configuration)

            // Send a Collect message and wait the response
            val result = instrumentStatusActor !! Collect

            result() match {
                case CollectedValue(keyword, value, comment, 0) :: Nil
                    => keyword should equal(fitsKeyword)
                    value should equal(defaultValue)
                    comment should be("Current global status of the instrument")
                case x: AnyRef => fail("Should not reply other message ")
            }

            // verify mock
            there was one(statusDB).getStatusItem(statusItemName)
        }
    }

    def buildConfiguration(statusItem: String): GDSConfiguration = {
        GDSConfiguration(Instrument("GPI"),
            GDSEvent("OBS_START_ACQ"),
            fitsKeyword,
            HeaderIndex(0),
            DataType("DOUBLE"),
            Mandatory(false),
            DefaultValue(defaultValue),
            Subsystem("STATUS"),
            Channel(statusItem),
            ArrayIndex("NULL"),
            FitsComment("Current global status of the instrument"))
    }
}

