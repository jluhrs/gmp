package edu.gemini.aspen.gds.epics

import java.util.logging.Logger
import edu.gemini.epics.{EpicsChannel, NewEpicsReader}
import edu.gemini.aspen.gds.api._

/**
 * Very simple actor that can produce as a reply of a Collect request a single value
 * linked to a single fitsKeyword
 */
class NewEpicsValuesActor(epicsReader: NewEpicsReader, configuration: GDSConfiguration) extends OneItemKeywordValueActor(configuration) {
    private val LOG = Logger.getLogger(this.getClass.getName)

    override def collectValues(): List[CollectedValue[_]] = {
        val readValue = Option(epicsReader.getChannel(sourceChannel))
        readValue map (extractEpicsItem) map (valueToCollectedValue) orElse (defaultCollectedValue) toList
    }

    def extractEpicsItem(epicsValue: EpicsChannel[_]) {
        if (epicsValue.isArray) {
            epicsValue.getArrayValue(arrayIndex)
        } else {
            epicsValue.getValue
        }
    }

}
