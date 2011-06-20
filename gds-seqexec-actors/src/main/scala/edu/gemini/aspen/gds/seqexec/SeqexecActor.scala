package edu.gemini.aspen.gds.seqexec


import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.staticheaderreceiver.TemporarySeqexecKeywordsDatabase
import edu.gemini.aspen.gds.staticheaderreceiver.TemporarySeqexecKeywordsDatabaseImpl.Retrieve
import edu.gemini.aspen.gds.api._
import scala.Option._
import java.util.logging.Logger

/**
 * Very simple actor that can produce as a reply of a Collect request a single value
 * linked to a single fitsKeyword
 */
class SeqexecActor(seqexecKeyDB: TemporarySeqexecKeywordsDatabase, dataLabel: DataLabel, configuration: GDSConfiguration) extends OneItemKeywordValueActor(configuration) {
    private val LOG = Logger.getLogger(this.getClass.getName)

    override def collectValues(): List[CollectedValue[_]] = {
        val seqexecValue = (seqexecKeyDB !? Retrieve(dataLabel, fitsKeyword)).asInstanceOf[Option[Any]]

        List(seqexecValue map (collectedValue) getOrElse (defaultCollectedValue))
    }

    def buildCollectedValue(d: Any): CollectedValue[_] = {
        dataType.name match {
            case "STRING" => CollectedValue(fitsKeyword, d.toString, fitsComment, headerIndex)
            case "DOUBLE" => CollectedValue(fitsKeyword, d.asInstanceOf[Double], fitsComment, headerIndex)
            case "INT" => CollectedValue(fitsKeyword, d.asInstanceOf[Int], fitsComment, headerIndex)
            case _ => null
        }
    }

    def collectedValue(d: Any): CollectedValue[_] = {
        try {
            buildCollectedValue(d)
        } catch {
            case e: ClassCastException => {
                LOG.warning("Data for " + fitsKeyword + " keyword was not of the type " + dataType.name + " specified in config file.")
                null
            }
        }
    }
}
