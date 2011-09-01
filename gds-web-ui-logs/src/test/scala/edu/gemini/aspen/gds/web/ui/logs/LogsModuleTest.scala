package edu.gemini.aspen.gds.web.ui.logs

import org.junit.Test
import org.junit.Assert._
import org.specs2.mock.Mockito

/**
 * Construction tests
 */
class LogsModuleTest extends Mockito {
  @Test
  def testBuildPanel = {
    // mock log service
    val logSource = mock[LogSource]
    val module = new LogsModule(logSource)
  }
}