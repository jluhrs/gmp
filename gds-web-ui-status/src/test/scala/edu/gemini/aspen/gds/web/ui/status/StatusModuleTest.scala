package edu.gemini.aspen.gds.web.ui.status

import model.InMemoryObservationsSource
import org.junit.Assert._
import com.vaadin.Application
import org.scalatest.FunSuite
import org.scalatest.mock.MockitoSugar
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class StatusModuleTest extends FunSuite with MockitoSugar {
  test("build panel") {
    val source = new InMemoryObservationsSource()
    val module = new StatusModule(source)

    val app = mock[Application]
    assertNotNull(module.buildTabContent(app))
  }

  test("build label") {
    assertEquals("Some label", StatusModule.buildLabel("Some label").getCaption)
  }

}