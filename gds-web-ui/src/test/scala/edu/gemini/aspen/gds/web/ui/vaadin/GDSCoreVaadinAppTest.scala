package edu.gemini.aspen.gds.web.ui.vaadin

import org.junit.Test
import org.junit.Assert._
import edu.gemini.aspen.gds.web.ui.api.StatusPanel

/**
 * Trivial tests
 */
class GDSCoreVaadinAppTest {

  @Test
  def testBuildUserPanel = {
    val statusPanel = new StatusPanelImpl()
    val panel = new GDSCoreVaadinApp(statusPanel).buildUserPanel
    assertNotNull(panel)
  }
}