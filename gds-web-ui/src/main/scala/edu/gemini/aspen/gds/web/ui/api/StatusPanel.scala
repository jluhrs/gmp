package edu.gemini.aspen.gds.web.ui.api

import com.vaadin.ui._

/**
 * Definition of a service that is capable of building a simple status panel component
 */
trait StatusPanel {
  /**
   * Builds a component to be used as a status panel
   */
  def buildStatusPanel: Component

  /**
   * Called when the module content needs to be refreshed
   */
  def refresh: Unit
}