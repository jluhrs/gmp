package edu.gemini.aspen.gds.web.ui.vaadin

import org.junit.Test
import org.junit.Assert._
import com.vaadin.ui.Panel
import scala.collection.JavaConversions._

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
}