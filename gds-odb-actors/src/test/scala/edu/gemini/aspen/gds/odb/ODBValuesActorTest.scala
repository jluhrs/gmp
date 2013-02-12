package edu.gemini.aspen.gds.odb

import org.junit.Assert._
import org.junit.Test
import org.scalatest.FunSuite
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.api.Conversions._
import edu.gemini.spModel.gemini.obscomp.SPProgram
import edu.gemini.pot.spdb.IDBDatabaseService
import edu.gemini.pot.sp.{ISPProgram, SPProgramID}
import edu.gemini.aspen.gds.api._
import fits.FitsKeyword
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._

class ODBValuesActorTest extends FunSuite with MockitoSugar {
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
  when(databaseService.lookupProgramByID(programID)).thenReturn(ispProgram)
  when(ispProgram.getDataObject.asInstanceOf[SPProgram]).thenReturn(spProgram)

  // Return type must be Any to avoid a compiler bug
  def buildActorAndCollect(configuration: List[GDSConfiguration]): Any = {
    val odbValuesActor = new ODBValuesActor(programIDLabel, databaseService, configuration)

    // Send a Collect message
    val result = odbValuesActor !! Collect

    result().asInstanceOf[List[CollectedValue[_]]]
  }

  test("normal collection") {
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
    verify(databaseService).lookupProgramByID(programID)
  }

  // should not return anything if the value cannot be read. The default will be added by an PostProcessingPolicy
  // it doesn't matter at this point if the item is mandatory or not
  @Test
  def testFlagUnknownChannels() {
    val configuration = buildConfigurationItem(firstNameFitsKeyword, "odb:achannel", "PI Last Name", true)

    val result = buildActorAndCollect(configuration)

    result match {
      case Nil =>
      case _ => fail("Should not reply other message ")
    }

    // verify mock
    verify(databaseService).lookupProgramByID(programID)
  }

  @Test
  def testMultipleItems() {
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
    verify(databaseService).lookupProgramByID(programID)
  }

  // should not return anything if the value cannot be read. The default will be added by an PostProcessingPolicy
  // it doesn't matter at this point if the item is mandatory or not
  @Test
  def testMandatoryItemNotFound() {
    val piInfo = new SPProgram.PIInfo(null, null, null, null, null)
    spProgram.setPIInfo(piInfo)

    val configuration = buildConfigurationItem(firstNameFitsKeyword, firstNameChannel, "PI Last Name", true)

    val result = buildActorAndCollect(configuration)

    result match {
      case Nil =>
      case _ => fail("Should not reply other message ")
    }
    // verify mock
    verify(databaseService).lookupProgramByID(programID)
  }

  // should not return anything if the value cannot be read. The default will be added by an PostProcessingPolicy
  // it doesn't matter at this point if the item is mandatory or not
  @Test
  def testNonMandatoryItemNotFound() {
    val piInfo = new SPProgram.PIInfo(null, null, null, null, null)
    spProgram.setPIInfo(piInfo)
    val configuration = buildConfigurationItem(firstNameFitsKeyword, firstNameChannel, "PI Last Name", false)

    val result = buildActorAndCollect(configuration)

    result match {
      case Nil =>
      case _ => fail("Should not reply other message ")
    }

    // verify mock
    verify(databaseService).lookupProgramByID(programID)
  }

  // should not return anything if the value cannot be read. The default will be added by an PostProcessingPolicy
  // it doesn't matter at this point if the item is mandatory or not
  @Test
  def testProgramNotFound() {
    when(databaseService.lookupProgramByID(programID)).thenReturn(null)
    val configuration = buildConfigurationItem(firstNameFitsKeyword, firstNameChannel, "PI First Name", true)

    val result = buildActorAndCollect(configuration)

    result match {
      case Nil =>
      case _ => fail("Should not reply other message ")
    }

    // verify mock
    verify(databaseService).lookupProgramByID(programID)
  }

  @Test
  def testTypeMismatch() {
    val configuration = GDSConfiguration("GPI", "OBS_START_ACQ", firstNameFitsKeyword, 0, "DOUBLE", true, "NOT FOUND", "ODB", firstNameChannel, 0, "", "PI First Name")

    val result = buildActorAndCollect(List(configuration))

    result match {
      case ErrorCollectedValue(keyword, error, comment, 0) :: Nil => {
        assertEquals(firstNameFitsKeyword, keyword)
        assertEquals(CollectionError.TypeMismatch, error)
        assertEquals("PI First Name", comment)
      }
      case _ => fail("Should not reply other message ")
    }

    // verify mock
    verify(databaseService).lookupProgramByID(programID)
  }


  def buildConfigurationItem(fitsKeyword: FitsKeyword, channelName: String, comment: String, mandatory: Boolean) = {
    List(GDSConfiguration("GPI", "OBS_START_ACQ", fitsKeyword, 0, "STRING", mandatory, "NOT FOUND", "ODB", channelName, 0, "", comment))
  }

}