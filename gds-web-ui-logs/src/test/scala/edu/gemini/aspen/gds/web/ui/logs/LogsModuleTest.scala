package edu.gemini.aspen.gds.web.ui.logs

import org.junit.Test
import org.junit.Assert._
import org.specs2.mock.Mockito
import com.vaadin.Application

/**
 * Construction tests
 */
class LogsModuleTest extends Mockito {
  @Test
  def testBuildPanel {
    // mock log service
    val logSource = mock[LogSource]
    val module = new LogsModule(logSource)
    val app = mock[Application]
    assertNotNull(module.buildTabContent(app))
  }
}