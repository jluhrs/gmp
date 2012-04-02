package edu.gemini.aspen.gds.web.ui.logs

import org.mockito.Mockito._
import org.junit.Assert._
import com.vaadin.Application
import org.scalatest.mock.MockitoSugar
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * Construction tests
 */
@RunWith(classOf[JUnitRunner])
class LogsModuleTest extends FunSuite with MockitoSugar
{
  test("build panel") {
    // mock log service
    val logSource = mock[LogSource]
    when(logSource.logEvents).thenReturn(Nil)
    val module = new LogsModule(logSource)
    val app = mock[Application]
    assertNotNull(module.buildTabContent(app))
  }
}