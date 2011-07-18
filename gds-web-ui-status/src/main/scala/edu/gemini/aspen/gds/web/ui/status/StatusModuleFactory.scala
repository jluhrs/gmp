package edu.gemini.aspen.gds.web.ui.status

import edu.gemini.aspen.gds.web.ui.api.GDSWebModuleFactory
import edu.gemini.aspen.giapi.status.StatusDatabaseService
import org.apache.felix.ipojo.annotations.{Requires, Provides, Instantiate, Component}
import edu.gemini.aspen.gds.observationstate.ObservationStateProvider

@Component
@Instantiate
@Provides(specifications = Array(classOf[GDSWebModuleFactory]))
class StatusModuleFactory(@Requires statusDB: StatusDatabaseService, @Requires obsState: ObservationStateProvider) extends GDSWebModuleFactory {
    override def buildWebModule = new StatusModule(statusDB, obsState)

    override protected def canEqual(other: Any): Boolean = other.isInstanceOf[StatusModuleFactory]
}