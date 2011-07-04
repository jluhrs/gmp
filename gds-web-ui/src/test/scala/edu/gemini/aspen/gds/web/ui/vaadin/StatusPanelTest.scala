package edu.gemini.aspen.gds.web.ui.vaadin

import org.junit.Test
import org.junit.Assert._
import scala.collection.JavaConversions._
import edu.gemini.aspen.gds.web.ui.api.StatusPanelModule
import com.vaadin.ui.Panel

/**
 * Trivial tests
 */
class StatusPanelTest {
  @Test
  def testBuildPanel = {
    val panel = new StatusPanelImpl().buildStatusPanel
    assertNotNull(panel)
    assertEquals(0, panel.getComponentIterator.toList.size)
  }

  @Test
  def testBuildPanelWithOneModule = {
    val statusPanel = new StatusPanelImpl()
    statusPanel.bindStatusPanelModule(new StatusPanelModule {
      def buildModule = new Panel()
    })
    val panel = statusPanel.buildStatusPanel
    assertNotNull(panel)
    assertEquals(1, panel.getComponentIterator.toList.size)
  }
}