package edu.gemini.aspen.gds.web.ui.api

import com.vaadin.ui.{Window, Panel, Component}

/**
 * A factory of GDSWebModules of a certain type.
 * It should return a new GDSWebModule for each invocation
 *
 * It should be registered as a an OSGI service
 */
trait GDSWebModuleFactory {
  lazy val name = this.getClass.getSimpleName
  /**
   * Creates a new module for a session
   */
  def buildWebModule: GDSWebModule

  override def toString = name

  override def equals(other: Any): Boolean = other match {
    case that: GDSWebModuleFactory => (that canEqual this) && name == that.name
    case _ => false
  }

  // Used by equals and can be overridden by extensions
  protected def canEqual(other: Any): Boolean = other.isInstanceOf[GDSWebModuleFactory]

  override def hashCode: Int = 41 * name.##
}