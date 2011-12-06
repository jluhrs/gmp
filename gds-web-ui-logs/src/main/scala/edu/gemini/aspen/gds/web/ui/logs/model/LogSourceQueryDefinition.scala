package edu.gemini.aspen.gds.web.ui.logs.model

import edu.gemini.aspen.gds.web.ui.logs.LogSource
import org.vaadin.addons.lazyquerycontainer.LazyQueryDefinition
import com.vaadin.data.Container.Filter
import edu.gemini.aspen.giapi.web.ui.vaadin.data._
import annotation.tailrec

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
    applyFilters(filters, logs)
  }

  @tailrec
  private def applyFilters(filters: List[Filter], logs: Iterable[LogEventWrapper]): Iterable[LogEventWrapper] = {
    filters match {
      case f:Filter => filter(f, logs)
      case f :: tail => applyFilters (tail, logs)
      case Nil => Nil
    }
  }

  def filter(f: Filter, logs: Iterable[LogEventWrapper]) = {
    def filterCondition(log:LogEventWrapper)(prop:String, acc:Boolean) = acc && f.appliesToProperty(prop) && f.passesFilter(Nil, referenceItem(prop, log))
    logs filter {
        l => filterableProperties.foldRight(true) {
          (prop, acc) => filterCondition(l)(prop, acc)
        }
    }
  }


  private def referenceItem(property:String, log:LogEventWrapper) = property match {
    case "level" => Item(property -> log.level)
    case _ => Item(property -> "")
  }

}