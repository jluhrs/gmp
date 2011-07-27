package edu.gemini.aspen.gds.web.ui.vaadin

import org.ops4j.pax.web.extender.whiteboard.runtime.DefaultResourceMapping
import org.ops4j.pax.web.extender.whiteboard.ResourceMapping
import javax.servlet.Filter
import org.eclipse.jetty.servlets.GzipFilter
import org.apache.felix.ipojo.annotations.{ServiceProperty, Provides, Instantiate, Component}

/**
 * Resource mapper used to serve css from inside the bundle
 */
@Component
@Instantiate
@Provides(specifications = Array(classOf[Filter]))
class GDSGZipFilter extends GzipFilter {
  @ServiceProperty(name = "filter-name", value = "gzipfilter")
  val label: String = "gzipfilter"
  @ServiceProperty(name = "urlPatterns")
  val urlPatterns: Array[String] = Array("/gds", "/VAADIN")
  @ServiceProperty(name = "servletNames")
  val servletName: Array[String] = Array("GDSVaadinServlet", "VaadinResourcesServlet")
}
