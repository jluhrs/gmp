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
import edu.gemini.aspen.gds.keywords.database.ProgramIdDatabaseImpl

@RunWith(classOf[JUnitRunner])
class ODBValuesActorSpec extends Spec with ShouldMatchers with Mockito {
    val dataLabel = new DataLabel("GS-2011")
    val queryRunner = mock[IDBQueryRunner]

    val lastNameChannel = "odb:piLastName"
    val firstNameChannel = "odb:piFirstName"
    val lastName = "Smith"
    val firstName = "John"
    val spProgram = new SPProgram()
    val piInfo = new SPProgram.PIInfo(firstName, lastName, null, null, null)
    spProgram.setPIInfo(piInfo)

    describe("An EpicsValuesActor") {
        it("should reply to Collect messages") {
            // mock return value
            queryRunner.queryPrograms(any[IDBQueryFunctor]) returns new MockGDSProgramQuery(spProgram)
            val fitsKeyword = new FitsKeyword("PIFSTNAM")
            val configuration = buildConfigurationItem(fitsKeyword, lastNameChannel, "PI Last Name")

            val odbValuesActor = new ODBValuesActor(queryRunner, List(configuration))

            // Send an init message
            val result = odbValuesActor !! Collect

            result() match {
                case CollectedValue(keyword, value, comment, 0) :: Nil
                => keyword should equal(fitsKeyword)
                value should equal(lastName)
                comment should be("PI Last Name")
                case x: AnyRef => fail("Should not reply other message ")
            }

            // verify mock
            there was queryRunner.queryPrograms(any[IDBQueryFunctor])
        }
        it("should ignore unknown channels") {
            // mock return value
            queryRunner.queryPrograms(any[IDBQueryFunctor]) returns new MockGDSProgramQuery(spProgram)
            val fitsKeyword = new FitsKeyword("PIFSTNAM")
            val configuration = buildConfigurationItem(fitsKeyword, "odb:achannel", "PI Last Name")

            val odbValuesActor = new ODBValuesActor(queryRunner, List(configuration))

            // Send an init message
            val result = odbValuesActor !! Collect

            result() match {
                case List() =>  // Expected
                case x: AnyRef => fail("Should not reply other message ")
            }

            // verify mock
            there was queryRunner.queryPrograms(any[IDBQueryFunctor])
        }
        it("should reply to Collect messages with multiple configurations") {
            // mock return value
            queryRunner.queryPrograms(any[IDBQueryFunctor]) returns new MockGDSProgramQuery(spProgram)
            val lastNameFitsKeyword = new FitsKeyword("PILSTNAM")
            val firstNameFitsKeyword = new FitsKeyword("PIFSTNAM")

            val configuration1 = buildConfigurationItem(lastNameFitsKeyword, lastNameChannel, "PI Last Name")
            val configuration2 = buildConfigurationItem(firstNameFitsKeyword, firstNameChannel, "PI First Name")

            val odbValuesActor = new ODBValuesActor(queryRunner, List(configuration1, configuration2))

            // Send an init message
            val result = odbValuesActor !! Collect

            result() match {
                case last :: first :: Nil => {
                    last match {
                        case CollectedValue (keyword, value, comment, 0)
                            =>  keyword should equal (lastNameFitsKeyword)
                                value should equal (lastName)
                                comment should be ("PI Last Name")
                    }
                    first match {
                        case CollectedValue (keyword, value, comment, 0)
                            =>  keyword should equal (firstNameFitsKeyword)
                                value should equal (firstName)
                                comment should be ("PI First Name")
                    }
                }
                case x: AnyRef => fail("Should not reply other message ")
            }

            // verify mock
            there was queryRunner.queryPrograms(any[IDBQueryFunctor])
        }
    }

    def buildConfigurationItem(fitsKeyword: FitsKeyword, channelName: String, comment: String) = {
        GDSConfiguration(Instrument("GPI"), GDSEvent("OBS_START_ACQ"), fitsKeyword, HeaderIndex(0), DataType("DOUBLE"), Mandatory(false), NullValue("NONE"), Subsystem("ODB"), Channel(channelName), ArrayIndex("NULL"), FitsComment(comment))
    }
}

class MockGDSProgramQuery(spProgram: SPProgram) extends GDSProgramQuery("programID") {
    override def program: Option[SPProgram] = Option(spProgram)

    def execute(odb: IDBDatabase, remoteNode: ISPRemoteNode) {
        // mocked method
    }
}

