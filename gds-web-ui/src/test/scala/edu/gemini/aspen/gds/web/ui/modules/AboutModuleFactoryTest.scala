package edu.gemini.aspen.gds.web.ui.modules

import org.junit.Test
import org.junit.Assert._
import com.vaadin.ui.Window

/**
 * Trivial tests
 */
class AboutModuleFactoryTest {
  @Test
  def testBuildPanel = {
    val module = new AboutModuleFactory().buildWebModule
    assertNotNull(module)
    assertNotNull(module.buildTabContent(new Window()))
  }

  @Test
  def testEqualityAndHash = {
    val module1 = new AboutModuleFactory()
    val module2 = new AboutModuleFactory()
    assertEquals(module1, module2)
    assertEquals(module1.##, module2.##)
  }

  @Test
  def testEqualityWithOtherModule = {
    val module1 = new AboutModuleFactory()
    val module2 = new HelpModuleFactory()
    assertFalse(module1 == module2)
    assertFalse(module1.## == module2.##)
  }
}