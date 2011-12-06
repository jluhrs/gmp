package edu.gemini.aspen.gds.web.ui.logs.model

import edu.gemini.aspen.gds.web.ui.logs.LogSource
import org.vaadin.addons.lazyquerycontainer.LazyQueryDefinition
import com.vaadin.data.Container.Filter
import edu.gemini.aspen.giapi.web.ui.vaadin.data._
import annotation.tailrec

/**
 * Placeholder class needed to let LoggingEventBeanQuery access logSource */
class LogSourceQueryDefinition(val logSource: LogSource, compositeItems: Boolean, batchSize: Int) extends LazyQueryDefinition(compositeItems, batchSize) {
  val filterableProperties = List[String]("level", "logger")
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
    applyFilters(filters, logs.toList)
  }

  private def applyFilters(filters: List[Filter], logs: List[LogEventWrapper]): List[LogEventWrapper] = {
    filters match {
      case Nil => logs
      case f :: tail => filter(f, logs) intersect applyFilters (tail, logs)
    }
  }

  def filter(f: Filter, logs: List[LogEventWrapper]) = {
    def folder(log:LogEventWrapper)(prop:String, acc:Boolean) = {
      // checks that the filter can process the property and that it accepts it
      acc && (!f.appliesToProperty(prop) || f.passesFilter(Nil, referenceItem(prop, log)))
    }
    logs filter {
        l => filterableProperties.foldRight(true) {
          (prop, acc) => folder(l)(prop, acc)
        }
    }
  }

  private def referenceItem(property:String, log:LogEventWrapper) = property match {
    case "level" => Item(property -> log.level)
    case "logger" => Item(property -> log.loggerName0)
    case _ => Item(property -> "")
  }

}