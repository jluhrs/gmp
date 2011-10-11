package edu.gemini.aspen.gds.web.ui.logs.model

import org.vaadin.addons.lazyquerycontainer._
import com.vaadin.data.Container.{Filter, Filterable}

/**
 * Needed for vaadin lazy query container to support filters */
class LogsContainer(queryDefinition: LogSourceQueryDefinition, queryFactory: QueryFactory) extends LazyQueryContainer(queryDefinition, queryFactory) with Filterable {
  def addContainerFilter(f: Filter) {
    queryDefinition.addContainerFilter(f)
  }

  def removeContainerFilter(f: Filter) {
    queryDefinition.removeContainerFilter(f)
  }

  def removeAllContainerFilters() {
    queryDefinition.removeAllContainerFilters()
  }
}