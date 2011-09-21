package edu.gemini.aspen.gds.web.ui.api

import com.vaadin.ui.Component

/**
 * Service capable of building and updating a single module displayed in the status module
 */
trait StatusPanelModule {
  val order: Int = 0

  def buildModule: Component

  /**
   * Called when the module content needs to be refreshed
   */
  def refresh(): Unit = {}
}