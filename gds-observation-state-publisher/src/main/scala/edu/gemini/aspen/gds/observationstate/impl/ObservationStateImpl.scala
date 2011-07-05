package edu.gemini.aspen.gds.observationstate.impl

import org.apache.felix.ipojo.annotations.{Requires, Provides, Instantiate, Component}
import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}
import edu.gemini.aspen.gds.api.CollectionError
import org.scala_tools.time.Imports
import edu.gemini.aspen.gds.observationstate.{ObservationStatePublisher, ObservationStateProvider, ObservationStateRegistrar}
import collection.mutable.HashMap
import collection.mutable.HashSet
import collection.mutable.Set

@Component
@Instantiate
@Provides(specifications = Array(classOf[ObservationStateRegistrar], classOf[ObservationStateProvider]))
class ObservationStateImpl(@Requires obsStatePubl: ObservationStatePublisher) extends ObservationStateRegistrar with ObservationStateProvider {

    class ObservationInfo {
        val missingKeywords: Set[FitsKeyword] = HashSet[FitsKeyword]()
        val errorKeywords: Set[(FitsKeyword, CollectionError.CollectionError)] = HashSet[(FitsKeyword, CollectionError.CollectionError)]()
        val times: Set[(AnyRef, Option[Imports.Duration])] = HashSet[(AnyRef, Option[Imports.Duration])]() //todo: think which is the correct type here
        var started = false
        var ended = false
    }

    val obsInfoMap = HashMap.empty[DataLabel, ObservationInfo]

    override def registerMissingKeyword(label: DataLabel, keyword: FitsKeyword) {
        obsInfoMap.getOrElseUpdate(label, new ObservationInfo).missingKeywords += keyword
    }

    override def registerCollectionError(label: DataLabel, keyword: FitsKeyword, error: CollectionError.CollectionError) {
        obsInfoMap.getOrElseUpdate(label, new ObservationInfo).errorKeywords += Tuple2(keyword, error)
    }

    override def registerTimes(label: DataLabel, times: Traversable[(AnyRef, Option[Imports.Duration])]) {
        obsInfoMap.getOrElseUpdate(label, new ObservationInfo).times ++= times
    }

    override def endObservation(label: DataLabel) {
        obsInfoMap.getOrElseUpdate(label, new ObservationInfo).ended = true
    }

    override def startObservation(label: DataLabel) {
        obsInfoMap.getOrElseUpdate(label, new ObservationInfo).started = true
    }


    //-----------------------------------------------------------------------

    override def getTimes(label: DataLabel): Traversable[(AnyRef, Option[Imports.Duration])] = {
        obsInfoMap.getOrElse(label, new ObservationInfo).times
    }

    override def getMissingKeywords(label: DataLabel): Traversable[FitsKeyword] = {
        obsInfoMap.getOrElse(label, new ObservationInfo).missingKeywords
    }

    override def getKeywordsInError(label: DataLabel): Traversable[(FitsKeyword, CollectionError.CollectionError)] = {
        obsInfoMap.getOrElse(label, new ObservationInfo).errorKeywords
    }

    override def getObservationsInProgress: Traversable[DataLabel] = {
        obsInfoMap filter {
            case (key, value) => (value.started && !value.ended)
        } keySet
    }
}