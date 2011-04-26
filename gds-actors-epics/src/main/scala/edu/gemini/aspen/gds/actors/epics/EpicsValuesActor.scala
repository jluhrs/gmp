package edu.gemini.aspen.gds.actors.epics

import edu.gemini.epics.EpicsReader
import edu.gemini.aspen.giapi.data.FitsKeyword
import edu.gemini.aspen.gds.actors.{KeywordValueActor, CollectedValue}

/**
 * Very simple actor that can produce as a reply of a Collect request a single value
 * linked to a single fitsKeyword
 */
class EpicsValuesActor(epicsReader: EpicsReader, fitsKeyword: FitsKeyword, channelName: String) extends KeywordValueActor {
    override def collectValues():List[CollectedValue] = {
        val epicsValue = epicsReader.getValue(channelName)
        CollectedValue(fitsKeyword, epicsValue, "") :: Nil
    }

}
