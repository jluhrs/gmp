package edu.gemini.aspen.gds.web.ui.logs

import org.junit.Test
import org.junit.Assert._
import com.vaadin.Application
import org.specs2.mock.Mockito

/**
 * Trivial tests
 */
class LogsModuleFactoryTest extends Mockito {
  @Test
  def testBuildPanel = {
    // mock log service
    val logsSource = mock[LogSource]
    logsSource.logEvents returns List()

    // test building the module
    val module = new LogsModuleFactory(logsSource).buildWebModule
    assertNotNull(module)
    val app = mock[Application]
    assertNotNull(module.buildTabContent(app))
  }

  @Test
  def testEqualityAndHash = {
    val logsSource = mock[LogSource]

    val module1 = new LogsModuleFactory(logsSource)
    val module2 = new LogsModuleFactory(logsSource)
    assertEquals(module1, module2)
    assertEquals(module1.##, module2.##)
  }
}