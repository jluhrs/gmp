package edu.gemini.aspen.gds.web.ui.vaadin

import org.ops4j.pax.web.extender.whiteboard.runtime.DefaultResourceMapping
import org.apache.felix.ipojo.annotations.{Provides, Instantiate, Component}
import org.ops4j.pax.web.extender.whiteboard.ResourceMapping

/**
 * Resource mapper used to serve css from inside the bundle
 */
@Component
@Instantiate
@Provides(specifications = Array(classOf[ResourceMapping]))
class CSSResources extends DefaultResourceMapping {
  setAlias("/VAADIN/themes/gds")
  setPath("/css")
}

