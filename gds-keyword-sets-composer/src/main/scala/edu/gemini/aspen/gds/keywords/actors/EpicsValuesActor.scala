package edu.gemini.aspen.gds.keywords.actors

import edu.gemini.epics.EpicsReader
import actors.Actor

class EpicsValuesActor(epicsReader: EpicsReader, channelNames: List[String]) extends Actor {
    def act() {
        val values = for (channel <- channelNames) yield {
            (channel, epicsReader.getValue(channel))
        }
        reply(values)
    }
}

