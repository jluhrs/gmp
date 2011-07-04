package edu.gemini.aspen.gds.observationstate.impl

import org.apache.felix.ipojo.annotations.{Requires, Provides, Instantiate, Component}
import edu.gemini.aspen.gds.api.configuration.GDSConfigurationService
import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}
import edu.gemini.aspen.gds.api.CollectionError
import org.scala_tools.time.Imports
import edu.gemini.aspen.gds.observationstate.{ObservationStatePublisher, ObservationStateProvider, ObservationStateRegistrar}

@Component
@Instantiate
@Provides(specifications = Array(classOf[ObservationStateRegistrar], classOf[ObservationStateProvider]))
class ObservationStateImpl(@Requires configService: GDSConfigurationService, @Requires obsStatePubl: ObservationStatePublisher) extends ObservationStateRegistrar with ObservationStateProvider {

    override def registerMissingKeyword(label: DataLabel, keyword: FitsKeyword) {}

    override def registerCollectionError(label: DataLabel, keyword: FitsKeyword, error: CollectionError.CollectionError) {}

    override def registerTimes(label: DataLabel, times: Map[AnyRef, Option[Imports.Duration]]) {}

    override def endObservation(label: DataLabel) {}

    override def startObservation(label: DataLabel) {}


    override def getTimes(label: DataLabel): Map[AnyRef, Option[Imports.Duration]] = null

    override def getMissingKeywords(label: DataLabel): Traversable[FitsKeyword] = null

    override def getKeywordsInError(label: DataLabel): Traversable[(FitsKeyword, CollectionError.CollectionError)] = null

    override def getObservationsInProgress: Traversable[DataLabel] = null
}