package edu.gemini.aspen.gds.web.ui.modules

import org.junit.Test
import org.junit.Assert._
import com.vaadin.Application

/**
 * Trivial tests
 */
class HelpModuleFactoryTest {
  @Test
  def testBuildPanel = {
    val module = new HelpModuleFactory().buildWebModule
    assertNotNull(module)
    assertNotNull(module.buildTabContent(new Application {
      def init() {}
    }))
  }

  @Test
  def testEqualityAndHash = {
    val module1 = new HelpModuleFactory()
    val module2 = new HelpModuleFactory()
    assertEquals(module1, module2)
    assertEquals(module1.##, module2.##)
  }
}