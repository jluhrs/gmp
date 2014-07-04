package edu.gemini.aspen.gds.web.ui.status

import edu.gemini.aspen.gds.web.ui.api.GDSWebModuleFactory
import edu.gemini.aspen.giapi.status.StatusItem
import model.ObservationsSource
import org.apache.felix.ipojo.annotations.{Requires, Provides, Instantiate, Component}
import edu.gemini.gmp.top.Top
import edu.gemini.aspen.giapi.status.dispatcher.{StatusItemFilter, FilteredStatusHandler}

/**
 * Factory class for StatusModule classes, one is created per user
 */
@Component
@Instantiate
@Provides(specifications = Array[Class[_]](classOf[GDSWebModuleFactory], classOf[FilteredStatusHandler]))
class StatusModuleFactory(@Requires top: Top, @Requires observationSource:ObservationsSource) extends GDSWebModuleFactory with FilteredStatusHandler {
  private val healthName = top.buildStatusItemName("gds:health")

  private lazy val module = new StatusModule(observationSource)

  override def buildWebModule = module

  override protected def canEqual(other: Any): Boolean = other match {
    case _: StatusModuleFactory => true
    case _ => false
  }

  def getName = "GDS Web Status Listener"

  def update[T](item: StatusItem[T]) {
    module.updateHealth(item)
  }

  def getFilter = new StatusItemFilter {
    def `match`(item: StatusItem[_]) = item.getName == healthName
  }
}