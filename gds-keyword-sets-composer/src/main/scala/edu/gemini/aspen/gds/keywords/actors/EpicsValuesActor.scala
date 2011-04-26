package edu.gemini.aspen.gds.keywords.actors

import edu.gemini.epics.EpicsReader
import edu.gemini.aspen.giapi.data.FitsKeyword
import edu.gemini.aspen.gds.keywordssets.{KeywordValueActor, Collect, CollectedValue}

/**
 * Very simple actor that can produce as a reply of a Collect request a single value
 * linked to a single fitsKeyword
 */
class EpicsValuesActor(epicsReader: EpicsReader, fitsKeyword: FitsKeyword, channelName: String) extends KeywordValueActor {
    start
    
    override def act() {
        loop {
            react {
                case Collect => reply(collectValue)
            }
        }
    }

    private def collectValue() = {
        val epicsValue = epicsReader.getValue(channelName)
        CollectedValue(fitsKeyword, epicsValue, "")
    }

}
