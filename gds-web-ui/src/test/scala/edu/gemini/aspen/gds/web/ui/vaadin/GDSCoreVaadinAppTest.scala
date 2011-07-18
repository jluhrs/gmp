package edu.gemini.aspen.gds.web.ui.vaadin

import org.junit.Test
import org.junit.Assert._
import edu.gemini.aspen.gds.web.ui.api.{GDSWebModule, GDSWebModuleFactory}
import com.vaadin.ui.{Window, Panel}
import com.vaadin.Application

/**
 * Trivial tests
 */
class GDSCoreVaadinAppTest {
  val statusPanel = new StatusPanelImpl()

  @Test
  def testBuildUserPanel = {
    val panel = new GDSCoreVaadinApp(statusPanel).buildUserPanel
    assertNotNull(panel)
  }

  @Test
  def testBuildBannerPanel = {
    val panel = new GDSCoreVaadinApp(statusPanel).buildBannerPanel
    assertNotNull(panel)
  }

  @Test
  def testTabsSortingWithoutFactories = {
    // the test will be applied to the functions sorting rather than to the tabSheet
    val app = new GDSCoreVaadinApp(statusPanel)
    assertTrue(app.sortModules.isEmpty)
    assertTrue(app.findTabsPositions(app.sortModules).isEmpty)
  }

  @Test
  def testTabsSortingWithFactories = {
    // the test will be applied to the functions sorting rather than to the tabSheet
    val app = new GDSCoreVaadinApp(statusPanel)
    val module0 = new DummyWebModule(0)
    val module1 = new DummyWebModule(1)
    // Add two factories
    app.bindGDSWebModule(new GDSWebModuleFactory {
      def buildWebModule = module0
    })
    app.bindGDSWebModule(new GDSWebModuleFactory {
      def buildWebModule = module1
    })
    // Check the calculated positions
    assertEquals(2, app.sortModules.size)
    assertEquals(2, app.findTabsPositions(app.sortModules).size)
    assertEquals(module0, app.sortModules (0)._1)
    assertEquals(module1, app.sortModules (1)._1)
  }

  @Test
  def testTabsSortingWithOneFactoryGone = {
    // the test will be applied to the functions sorting rather than to the tabSheet
    val app = new GDSCoreVaadinApp(statusPanel)
    val module0 = new DummyWebModule(0)
    val module1 = new DummyWebModule(1)
    val factory0 = new GDSWebModuleFactory {
      def buildWebModule = module0
    }
    // Add two factories
    app.bindGDSWebModule(factory0)
    app.bindGDSWebModule(new GDSWebModuleFactory {
      def buildWebModule = module1
    })
    // One factory gone
    app.unbindModule(factory0)

    // The order should be preserved
    assertEquals(1, app.sortModules.size)
    assertEquals(1, app.findTabsPositions(app.sortModules).size)
    assertEquals(module1, app.sortModules (0)._1)
  }

  class DummyWebModule(val order:Int) extends GDSWebModule {
    val title = "title"

    def buildTabContent(app: Application) = new Panel()
  }
}