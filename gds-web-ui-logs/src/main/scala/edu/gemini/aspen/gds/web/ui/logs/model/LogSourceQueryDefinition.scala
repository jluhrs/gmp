package edu.gemini.aspen.gds.web.ui.logs.model

import edu.gemini.aspen.gds.web.ui.logs.LogSource
import org.vaadin.addons.lazyquerycontainer.LazyQueryDefinition
import com.vaadin.data.Container.Filter
import com.vaadin.data.util.{PropertysetItem, ObjectProperty, BeanItem}

/**
 * Placeholder class needed to let LoggingEventBeanQuery access logSource */
class LogSourceQueryDefinition(val logSource: LogSource, compositeItems: Boolean, batchSize: Int) extends LazyQueryDefinition(compositeItems, batchSize) {
  val filterableProperties = List[String]("level")
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
          (l, p) => val i = new PropertysetItem();
            i.addItemProperty (l, new ObjectProperty[AnyRef](p))
            l &&  f.appliesToProperty(p) && f.passesFilter(p, i)
      }
    }
  }


}