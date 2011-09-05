package edu.gemini.aspen.gds.web.ui.logs.model

import org.vaadin.addons.lazyquerycontainer.LazyQueryDefinition
import edu.gemini.aspen.gds.web.ui.logs.LogSource
import com.vaadin.data.Container.{Filter, Filterable}
import com.vaadin.data.util.BeanItem
import com.vaadin.data.util.filter.Compare

/**
 * Placeholder class needed to let LoggingEventBeanQuery access logSource */
class LogSourceQueryDefinition(val logSource: LogSource, compositeItems: Boolean, batchSize: Int) extends LazyQueryDefinition(compositeItems, batchSize) {
  val filterableProperties = List("level")
  var filters = List[Filter]()

  def addContainerFilter(f: Filter) {
    filters = f :: filters
  }

  def removeContainerFilter(f: Filter) {
    filters = filters filterNot {
      _ == f
    }
  }

  def removeAllContainerFilters() {
    filters = Nil
  }

  def filterResults(logs: Iterable[LogEventWrapper]): Iterable[LogEventWrapper] = {
    var l = logs
    for (f <- filters) {
      l = filter(f, l)
    }
    l
  }

  private def filter(f: Filter, logs: Iterable[LogEventWrapper]) = {
    logs filter {
        x => filterableProperties.foldLeft(true) {
          (l, p) => l && (f.appliesToProperty(p) && f.passesFilter(p, new BeanItem[LogEventWrapper](x)))
      }
    }
  }


}