package edu.gemini.aspen.gds.odb

import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Spec
import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}
import org.specs2.mock.Mockito
import edu.gemini.aspen.gds.api._
import edu.gemini.spModel.gemini.obscomp.SPProgram
import edu.gemini.pot.sp.ISPRemoteNode
import edu.gemini.pot.spdb.{IDBQueryFunctor, IDBDatabase, IDBQueryRunner}

@RunWith(classOf[JUnitRunner])
class ODBValuesActorSpec extends Spec with ShouldMatchers with Mockito {
    describe("An EpicsValuesActor") {
        it("should reply to Collect messages") {
            // Generate dataset
            val dataLabel = new DataLabel("GS-2011")
            val queryRunner = mock[IDBQueryRunner]

            val channelName = "odb:piFirstName"
            val referenceValue = "Smith"
            val spProgram = new SPProgram()
            val piInfo = new SPProgram.PIInfo(referenceValue, null, null, null, null)
            spProgram.setPIInfo(piInfo)

            // mock return value
            queryRunner.queryPrograms(any[IDBQueryFunctor]) returns new MockGDSProgramQuery(spProgram)
            val fitsKeyword = new FitsKeyword("PIFSTNAM")

            val configuration = GDSConfiguration(Instrument("GPI"), GDSEvent("OBS_START_ACQ"), new FitsKeyword("PIFSTNAM"), HeaderIndex(0), DataType("DOUBLE"), Mandatory(false), NullValue("NONE"), Subsystem("ODB"), Channel(channelName), ArrayIndex("NULL"), FitsComment("Mean airmass for the observation"))

            val epicsValueActor = new ODBValuesActor(queryRunner, List(configuration))
            
            // Send an init message
            val result = epicsValueActor !! Collect

            result() match {
                case CollectedValue(keyword, value, comment, 0) :: Nil
                    => keyword should equal (fitsKeyword)
                       value should equal (referenceValue)
                       comment should be ("Mean airmass for the observation")
                       println(value)
                case x:AnyRef => fail("Should not reply other message ")
            }

            // verify mock
            there was  queryRunner.queryPrograms(any[IDBQueryFunctor])
        }
    }
}

class MockGDSProgramQuery(spProgram:SPProgram) extends GDSProgramQuery("programID") {
    override def program: Option[SPProgram] = Option(spProgram)

    def execute(odb: IDBDatabase, remoteNode: ISPRemoteNode) {
        // mocked method
    }
}

