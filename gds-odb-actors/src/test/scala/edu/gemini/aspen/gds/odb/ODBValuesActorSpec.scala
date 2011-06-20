package edu.gemini.aspen.gds.odb

import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.junit.Assert._
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Spec
import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}
import org.specs2.mock.Mockito
import edu.gemini.aspen.gds.api.Conversions._
import edu.gemini.spModel.gemini.obscomp.SPProgram
import edu.gemini.pot.spdb.IDBDatabaseService
import edu.gemini.pot.sp.{ISPProgram, SPProgramID}
import edu.gemini.aspen.gds.api._

@RunWith(classOf[JUnitRunner])
class ODBValuesActorSpec extends Spec with ShouldMatchers with Mockito {
    val dataLabel = new DataLabel("GS-2011")
    val programIDLabel = "programID"
    val programID = SPProgramID.toProgramID("programID")
    val databaseService = mock[IDBDatabaseService]

    val lastNameChannel = "odb:piLastName"
    val firstNameChannel = "odb:piFirstName"
    val lastName = "Smith"
    val firstName = "John"
    val spProgram = new SPProgram()
    val piInfo = new SPProgram.PIInfo(firstName, lastName, null, null, null)
    val ispProgram = mock[ISPProgram]
    val firstNameFitsKeyword = new FitsKeyword("PIFSTNAM")

    spProgram.setPIInfo(piInfo)
    databaseService.lookupProgramByID(programID) returns ispProgram
    ispProgram.getDataObject returns spProgram

    // Return type must be Any to avoid a compiler bug
    def buildActorAndCollect(configuration: List[GDSConfiguration]): Any = {
        val odbValuesActor = new ODBValuesActor(programIDLabel, databaseService, configuration)

        // Send a Collect message
        val result = odbValuesActor !! Collect

        result().asInstanceOf[List[CollectedValue[_]]]
    }

    describe("An ODBValuesActor") {
        it("should reply to Collect messages") {
            val configuration = buildConfigurationItem(firstNameFitsKeyword, firstNameChannel, "PI First Name", true)

            val result = buildActorAndCollect(configuration)

            result match {
                case CollectedValue(keyword, value, comment, 0) :: Nil => {
                            assertEquals(firstNameFitsKeyword, keyword)
                            assertEquals(firstName, value)
                            assertEquals("PI First Name", comment)
                        }
                case _ => fail("Should not reply other message ")
            }

            // verify mock
            there was databaseService.lookupProgramByID(programID)
        }
        it("should ignore unknown channels") {
            val configuration = buildConfigurationItem(firstNameFitsKeyword, "odb:achannel", "PI Last Name", true)

            val result = buildActorAndCollect(configuration)

            result match {
                case ErrorCollectedValue(keyword, error, comment, 0) :: Nil =>  {
                    assertEquals(firstNameFitsKeyword, keyword)
                    assertEquals(CollectionError.MandatoryRequired, error)
                    assertEquals("PI Last Name", comment)
                }
                case _ => fail("Should not reply other message ")
            }

            // verify mock
            there was databaseService.lookupProgramByID(programID)
        }
        it("should reply to Collect messages with multiple configurations") {
            val lastNameFitsKeyword = new FitsKeyword("PILSTNAM")

            val configuration1 = buildConfigurationItem(lastNameFitsKeyword, lastNameChannel, "PI Last Name", true)
            val configuration2 = buildConfigurationItem(firstNameFitsKeyword, firstNameChannel, "PI First Name", true)

            val result = buildActorAndCollect(configuration1 ::: configuration2)

            result match {
                case last :: first :: Nil => {
                    last match {
                        case CollectedValue(keyword, value, comment, 0) => {
                            assertEquals(lastNameFitsKeyword, keyword)
                            assertEquals(lastName, value)
                            assertEquals("PI Last Name", comment)
                        }
                    }
                    first match {
                        case CollectedValue(keyword, value, comment, 0) => {
                            assertEquals(firstNameFitsKeyword, keyword)
                            assertEquals(firstName, value)
                            assertEquals("PI First Name", comment)
                        }
                    }
                }
                case _ => fail("Should not reply other message ")
            }

            // verify mock
            there was databaseService.lookupProgramByID(programID)
        }
        it("should produce an error if mandatory and the result is null") {
            val piInfo = new SPProgram.PIInfo(null, null, null, null, null)
            spProgram.setPIInfo(piInfo)

            val configuration = buildConfigurationItem(firstNameFitsKeyword, firstNameChannel, "PI Last Name", true)

            val result = buildActorAndCollect(configuration)

            result match {
                case ErrorCollectedValue(keyword, error, comment, 0) :: Nil =>  {
                    assertEquals(firstNameFitsKeyword, keyword)
                    assertEquals(CollectionError.MandatoryRequired, error)
                    assertEquals("PI Last Name", comment)
                }
                case _ => fail("Should not reply other message ")
            }
            // verify mock
            there was databaseService.lookupProgramByID(programID)
        }
        it("should give a default value if not mandatory and the result is null") {
            val piInfo = new SPProgram.PIInfo(null, null, null, null, null)
            spProgram.setPIInfo(piInfo)
            val configuration = buildConfigurationItem(firstNameFitsKeyword, firstNameChannel, "PI Last Name", false)

            val result = buildActorAndCollect(configuration)

            result match {
                case DefaultCollectedValue(keyword, value, comment, 0) :: Nil =>  {
                    assertEquals(firstNameFitsKeyword, keyword)
                    assertEquals("NOT FOUND", value)
                    assertEquals("PI Last Name", comment)
                }
                case _ => fail("Should not reply other message ")


                // verify mock
                there was databaseService.lookupProgramByID(programID)
            }
        }
        it("should be empty if there is no program") {
            databaseService.lookupProgramByID(programID) returns null
            val configuration = buildConfigurationItem(firstNameFitsKeyword, firstNameChannel, "PI First Name", true)

            val result = buildActorAndCollect(configuration)

            result match {
                case ErrorCollectedValue(keyword, error, comment, 0) :: Nil =>  {
                    assertEquals(firstNameFitsKeyword, keyword)
                    assertEquals(CollectionError.MandatoryRequired, error)
                    assertEquals("PI First Name", comment)
                }
                case _ => fail("Should not reply other message ")
            }

            // verify mock
            there was databaseService.lookupProgramByID(programID)
        }
    }

    def buildConfigurationItem(fitsKeyword: FitsKeyword, channelName: String, comment: String, mandatory: Boolean) = {
        List(GDSConfiguration("GPI", "OBS_START_ACQ", fitsKeyword, 0, "DOUBLE", mandatory, "NOT FOUND", "ODB", channelName, 0, comment))
    }


}