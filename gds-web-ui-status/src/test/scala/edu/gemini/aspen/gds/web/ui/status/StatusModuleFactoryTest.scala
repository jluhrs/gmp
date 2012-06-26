package edu.gemini.aspen.gds.web.ui.status

import model.InMemoryObservationsSource
import org.junit.Assert.assertNotNull
import com.vaadin.Application
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.mock.MockitoSugar

@RunWith(classOf[JUnitRunner])
class StatusModuleFactoryTest extends FunSuite with MockitoSugar {
  test("BuildPanel") {
    val source = new InMemoryObservationsSource()

    val module = new StatusModuleFactory(source).buildWebModule
    assertNotNull(module)
    val app = mock[Application]
    assertNotNull(module.buildTabContent(app))
  }
}