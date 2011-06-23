package edu.gemini.aspen.gds.obsevent.handler

import java.util.EnumSet
import edu.gemini.aspen.giapi.data.{DataLabel, ObservationEvent}

class ObsEventBookKeeping {
    private val obsEventsMap = collection.mutable.Map.empty[DataLabel, EnumSet[ObservationEvent]]
    private val repliesMap = collection.mutable.Map.empty[DataLabel, EnumSet[ObservationEvent]]

    def previousArrived(obsEvent: ObservationEvent, dataLabel: DataLabel) = {
        EnumSet.complementOf(EnumSet.range(obsEvent, ObservationEvent.values().last)).equals(obsEventsMap.getOrElse(dataLabel, EnumSet.noneOf(classOf[ObservationEvent])))
    }

    def obsArrived(obsEvent: ObservationEvent, dataLabel: DataLabel) = {
        obsEventsMap.getOrElse(dataLabel, EnumSet.noneOf(classOf[ObservationEvent])).contains(obsEvent)
    }

    def replyArrived(obsEvent: ObservationEvent, dataLabel: DataLabel) = {
        repliesMap.getOrElse(dataLabel, EnumSet.noneOf(classOf[ObservationEvent])).contains(obsEvent)
    }

    def allRepliesArrived(dataLabel: DataLabel) = {
        repliesMap.getOrElse(dataLabel, EnumSet.noneOf(classOf[ObservationEvent])).containsAll(EnumSet.allOf(classOf[ObservationEvent]))
    }

    def addReply(obsEvent: ObservationEvent, dataLabel: DataLabel) {
        val repliesSet = repliesMap.getOrElseUpdate(dataLabel, EnumSet.noneOf(classOf[ObservationEvent]))
        repliesSet.add(obsEvent)
    }

    def addObs(obsEvent: ObservationEvent, dataLabel: DataLabel) {
        val obsSet = obsEventsMap.getOrElseUpdate(dataLabel, EnumSet.noneOf(classOf[ObservationEvent]))
        obsSet.add(obsEvent)
    }

    def clean(dataLabel: DataLabel) {
        repliesMap -= dataLabel
        obsEventsMap -= dataLabel
    }
}