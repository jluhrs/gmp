package edu.gemini.aspen.gds.postprocessingpolicy

import org.junit.Assert._
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.api.Conversions._
import edu.gemini.aspen.gds.api._
import configuration.GDSConfigurationService
import org.mockito.Mockito.when
import org.scalatest.FunSuite
import org.scalatest.mock.MockitoSugar
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class EnforceMandatoryPolicyTest extends FunSuite with MockitoSugar {
  val dataLabel = new DataLabel("some key")

  test("no errors") {
    val config = mock[GDSConfigurationService]
    when(config.getConfiguration).thenReturn(Nil)

    val ep = new EnforceMandatoryPolicy(config)
    val collectedValues = CollectedValue[Double]("KEY1", 1.0, "comment", 0, None) :: Nil

    assertEquals(collectedValues, ep.applyPolicy(dataLabel, collectedValues))
  }

  test("with one error") {
    val config = mock[GDSConfigurationService]
    when(config.getConfiguration).thenReturn(Nil)

    val ep = new EnforceMandatoryPolicy(config)
    val collectedValues = CollectedValue[Double]("KEY1", 1.0, "comment", 0, None) :: ErrorCollectedValue("KEY2", CollectionError.MandatoryRequired, "comment", 0) :: Nil
    val filteredValues = CollectedValue[Double]("KEY1", 1.0, "comment", 0, None) :: ErrorCollectedValue("KEY2", CollectionError.MandatoryRequired, "comment", 0) :: Nil

    assertEquals(filteredValues, ep.applyPolicy(dataLabel, collectedValues))
  }

  test("with one mandatory item missing") {
    val config = mock[GDSConfigurationService]
    when(config.getConfiguration).thenReturn(GDSConfiguration("GPI", "OBS_START_ACQ", "KEY3", 0, "STRING", true, "default", "EPICS", "gpi:value", 0, "", "comment") :: Nil)

    val ep = new EnforceMandatoryPolicy(config)
    val collectedValues = CollectedValue[Double]("KEY1", 1.0, "comment", 0, None) :: Nil
    val filteredValues = CollectedValue[Double]("KEY1", 1.0, "comment", 0, None) :: ErrorCollectedValue("KEY3", CollectionError.MandatoryRequired, "comment", 0) :: Nil

    assertEquals(filteredValues, ep.applyPolicy(dataLabel, collectedValues))
  }
  test("with one non mandatory item missing") {
    val config = mock[GDSConfigurationService]
    when(config.getConfiguration).thenReturn(GDSConfiguration("GPI", "OBS_START_ACQ", "KEY3", 0, "STRING", false, "default", "EPICS", "gpi:value", 0, "", "comment") :: Nil)

    val ep = new EnforceMandatoryPolicy(config)
    val collectedValues = CollectedValue[Double]("KEY1", 1.0, "comment", 0, None) :: Nil
    val filteredValues = CollectedValue[Double]("KEY1", 1.0, "comment", 0, None) :: ErrorCollectedValue("KEY3", CollectionError.ItemNotFound, "comment", 0) :: Nil

    assertEquals(filteredValues, ep.applyPolicy(dataLabel, collectedValues))
  }

  test("ignore instrument keywords, fix for GIAPI-867") {
    val config = mock[GDSConfigurationService]
    when(config.getConfiguration).thenReturn(GDSConfiguration("GPI", "OBS_START_ACQ", "KEY2", 0, "STRING", true, "default", "INSTRUMENT", "gpi:value", 0, "", "comment") :: Nil)

    val ep = new EnforceMandatoryPolicy(config)
    val collectedValues = Nil
    val filteredValues = Nil

    assertEquals(filteredValues, ep.applyPolicy(dataLabel, collectedValues))
  }

}