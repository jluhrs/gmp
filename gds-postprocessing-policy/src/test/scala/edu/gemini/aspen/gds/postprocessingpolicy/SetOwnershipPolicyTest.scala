package edu.gemini.aspen.gds.postprocessingpolicy

import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.api.Conversions._
import edu.gemini.aspen.gds.api._
import configuration.GDSConfigurationService
import org.mockito.Mockito.when
import org.scalatest.FunSuite
import org.scalatest.mock.MockitoSugar
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import java.io.File

@RunWith(classOf[JUnitRunner])
class SetOwnershipPolicyTest extends FunSuite with MockitoSugar {
  val dataLabel = new DataLabel("some key")

  test("ownership set") {
    val config = mock[GDSConfigurationService]
    when(config.getConfiguration).thenReturn(GDSConfiguration("GPI", "OBS_START_ACQ", "KEY1", 0, "STRING", true, "default", "EPICS", "gpi:value", 0, "", "comment") :: GDSConfiguration("GPI", "OBS_START_ACQ", "KEY2", 0, "STRING", false, "default", "EPICS", "gpi:value", 0, "", "comment") :: Nil)

    val ep = new SetOwnershipPolicy("user", "false")
    val of = File.createTempFile("pre", "suf")
    val ff = File.createTempFile("pre", "suf")
    ep.fileReady(of, ff)

    of.deleteOnExit()
    ff.deleteOnExit()
  }

}