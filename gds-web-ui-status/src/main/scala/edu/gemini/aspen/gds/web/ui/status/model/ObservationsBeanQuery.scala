package edu.gemini.aspen.gds.web.ui.status.model

import java.util.List
import java.util.logging.Logger
import java.lang.UnsupportedOperationException
import org.vaadin.addons.lazyquerycontainer._
import scala.collection.JavaConversions._

/**
 * Placeholder class needed to let LoggingEventBeanQuery access logSource */
class ObservationSourceQueryDefinition(val obsSource: ObservationsSource, compositeItems: Boolean, batchSize: Int) extends LazyQueryDefinition(compositeItems, batchSize)

/**
 * This class is used by the LazyQueryContainer to read beans representing log values to display on the screen
 * The BeanQuery in this case is read only */
class ObservationsBeanQuery(queryDefinition0: QueryDefinition, queryConfiguration: java.util.Map[String, Object], sortPropertyIds: Array[Object], sortStates: Array[Boolean]) extends AbstractBeanQuery[ObservationBean](queryDefinition0, queryConfiguration, sortPropertyIds, sortStates) {
  val LOG = Logger.getLogger(this.getClass.getName)

  val observationsSource: ObservationSourceQueryDefinition = queryDefinition0 match {
    case q: ObservationSourceQueryDefinition => q
    case _ => sys.error("Should not happen")
  }

  override def saveBeans(p1: List[ObservationBean], p2: List[ObservationBean], p3: List[ObservationBean]) {
    throw new UnsupportedOperationException()
  }

  override def loadBeans(startIndex: Int, count: Int):java.util.List[ObservationBean] = {
    println("load mean")
    val result = filteredObservations drop (startIndex - 1) take (count)

    val sortProperties = sortPropertyIds.headOption.getOrElse("timeStamp").toString
    val ascending = sortStates.headOption.getOrElse(true)

    /*val sortedLog = result sortBy LoggingEventBeanQuery.sortingFunctions(sortProperties)

    if (ascending) {
      sortedLog
    } else {
      sortedLog reverse
    }*/
    result.toList
  }

  override def size(): Int = Option(filteredObservations).getOrElse(Nil).size

  override def constructBean() = throw new UnsupportedOperationException()

  private def filteredObservations = observationsSource.obsSource.observations
}

