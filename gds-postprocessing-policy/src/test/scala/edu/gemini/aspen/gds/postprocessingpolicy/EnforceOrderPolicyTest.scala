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
class EnforceOrderPolicyTest extends FunSuite with MockitoSugar {
  val dataLabel = new DataLabel("some key")

  test("order ordered") {
    val config = mock[GDSConfigurationService]
    when(config.getConfiguration).thenReturn(GDSConfiguration("GPI", "OBS_START_ACQ", "KEY1", 0, "STRING", true, "default", "EPICS", "gpi:value", 0, "", "comment") :: GDSConfiguration("GPI", "OBS_START_ACQ", "KEY2", 0, "STRING", false, "default", "EPICS", "gpi:value", 0, "", "comment") :: Nil)

    val ep = new EnforceOrderPolicy(config)
    val collectedValues = CollectedValue[Double]("KEY1", 1.0, "comment", 0, None) ::CollectedValue[Double]("KEY2", 1.0, "comment", 0, None):: Nil
    val orderedValues = CollectedValue[Double]("KEY1", 1.0, "comment", 0, None) ::CollectedValue[Double]("KEY2", 1.0, "comment", 0, None):: Nil

    assertEquals(orderedValues, ep.applyPolicy(dataLabel, collectedValues))
  }

  test("order unordered") {
    val config = mock[GDSConfigurationService]
    when(config.getConfiguration).thenReturn(GDSConfiguration("GPI", "OBS_START_ACQ", "KEY1", 0, "STRING", true, "default", "EPICS", "gpi:value", 0, "", "comment") :: GDSConfiguration("GPI", "OBS_START_ACQ", "KEY2", 0, "STRING", false, "default", "EPICS", "gpi:value", 0, "", "comment") :: Nil)

    val ep = new EnforceOrderPolicy(config)
    val collectedValues = CollectedValue[Double]("KEY2", 1.0, "comment", 0, None) ::CollectedValue[Double]("KEY1", 1.0, "comment", 0, None):: Nil
    val orderedValues = CollectedValue[Double]("KEY1", 1.0, "comment", 0, None) ::CollectedValue[Double]("KEY2", 1.0, "comment", 0, None):: Nil

    assertEquals(orderedValues, ep.applyPolicy(dataLabel, collectedValues))
  }

}