package edu.gemini.aspen.gds.web.ui.vaadin

import org.ops4j.pax.web.extender.whiteboard.runtime.DefaultResourceMapping
import org.apache.felix.ipojo.annotations.{Provides, Instantiate, Component}
import org.ops4j.pax.web.extender.whiteboard.ResourceMapping

/**
 * Resource mapper used to serve images from inside the bundle
 */
@Component
@Instantiate
@Provides(specifications = Array(classOf[ResourceMapping]))
class ImageResources extends DefaultResourceMapping {
    setAlias("/gds/APP/1")
    setPath("/images")
}

