package edu.gemini.aspen.gds.web.ui.status.model

import org.vaadin.addons.lazyquerycontainer.LazyQueryDefinition

/**
 * Placeholder class needed to let LoggingEventBeanQuery access logSource */
class ObservationSourceQueryDefinition(val obsSource: ObservationsSource, compositeItems: Boolean, batchSize: Int) extends LazyQueryDefinition(compositeItems, batchSize) {

}