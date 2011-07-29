package edu.gemini.aspen.gds.web.ui.vaadin

import javax.servlet.Filter
import org.eclipse.jetty.servlets.GzipFilter
import org.apache.felix.ipojo.annotations.{ServiceProperty, Provides, Instantiate, Component}

/**
 * Filter that will apply gzip compression to the web ui
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