package edu.gemini.aspen.gds.errorpolicy

import org.junit.Test
import org.junit.Assert._
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.api.Conversions._
import edu.gemini.aspen.gds.api._
import configuration.GDSConfigurationService
import org.mockito.Mockito.{when, mock}

class EnforceMandatoryPolicyTest {
  val dataLabel = new DataLabel("some name")

  @Test
  def testNonErrors() {
    val config = mock(classOf[GDSConfigurationService])
    when(config.getConfiguration).thenReturn(Nil)

    val ep = new EnforceMandatoryPolicy(config)
    val collectedValues = CollectedValue[Double]("KEY1", 1.0, "comment", 0) :: Nil

    assertEquals(collectedValues, ep.applyPolicy(dataLabel, collectedValues))
  }

  @Test
  def testWithError() {
    val config = mock(classOf[GDSConfigurationService])
    when(config.getConfiguration).thenReturn(Nil)

    val ep = new EnforceMandatoryPolicy(config)
    val collectedValues = CollectedValue[Double]("KEY1", 1.0, "comment", 0) :: ErrorCollectedValue("KEY2", CollectionError.MandatoryRequired, "comment", 0) :: Nil
    val filteredValues = CollectedValue[Double]("KEY1", 1.0, "comment", 0) :: CollectedValue("KEY2", "", "comment", 0) :: Nil

    assertEquals(filteredValues, ep.applyPolicy(dataLabel, collectedValues))
  }

  @Test
  def testWithMissing() {
    val config = mock(classOf[GDSConfigurationService])
    when(config.getConfiguration).thenReturn(GDSConfiguration("GPI", "OBS_START_ACQ", "KEY2", 0, "STRING", true, "default", "EPICS", "gpi:value", 0, "comment") :: GDSConfiguration("GPI", "OBS_START_ACQ", "KEY3", 0, "STRING", false, "default", "EPICS", "gpi:value", 0, "comment") :: Nil)


    val ep = new EnforceMandatoryPolicy(config)
    val collectedValues = CollectedValue[Double]("KEY1", 1.0, "comment", 0) :: Nil
    val filteredValues = CollectedValue[Double]("KEY1", 1.0, "comment", 0) :: CollectedValue("KEY2", "", "comment", 0) :: DefaultCollectedValue("KEY3", "default", "comment", 0) :: Nil

    assertEquals(filteredValues, ep.applyPolicy(dataLabel, collectedValues))
  }

}