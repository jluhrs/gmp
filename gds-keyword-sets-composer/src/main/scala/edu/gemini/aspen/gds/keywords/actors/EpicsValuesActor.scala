package edu.gemini.aspen.gds.keywords.actors

import edu.gemini.epics.EpicsReader
import actors.Actor
import edu.gemini.aspen.giapi.data.FitsKeyword
import edu.gemini.aspen.gds.keywordssets.{Collect, CollectedValue}

/**
 * Very simple actor that can produce as a reply of a Collect request a single value
 * linked to a single fitsKeyword
 */
class EpicsValuesActor(epicsReader: EpicsReader, fitsKeyword: FitsKeyword, channelName: String) extends Actor {
    def act() {
        loop {
            react {
                case Collect => reply(CollectedValue(fitsKeyword, epicsReader.getValue(channelName), ""))
            }
        }
    }

}

