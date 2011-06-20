package edu.gemini.aspen.gds.odb

import edu.gemini.spModel.gemini.obscomp.SPProgram
import edu.gemini.pot.sp.SPProgramID
import edu.gemini.pot.spdb.IDBDatabaseService
import edu.gemini.aspen.gds.api._
import org.apache.log4j.Logger

/**
 * Actor that can produce as a reply of a Collect request a set of CollectedValues obtained from a Query
 * to the ODB. The query is in the functor below and basically consists of retrieving the program
 * for a given programID
 */
class ODBValuesActor(programID: String, queryRunner: IDBDatabaseService, configuration: List[GDSConfiguration]) extends KeywordValueActor {
    type ExtractorFunction = SPProgram => Option[AnyRef]
    type CollectorFunction = SPProgram => CollectedValue[_]

    // Defines a list of "channels" to functions map that can extract information from a SPProgram instance
    // More methods can be added to the list to support more channels
    val extractorFunctions = Map[String, ExtractorFunction](
        "odb:piLastName" -> extractPILastName,
        "odb:piFirstName" -> extractPIFirstName)

    start()

    override def collectValues(): List[CollectedValue[_]] = {
        // Do the ODB query
        val progId = SPProgramID.toProgramID(programID)
        val dataObjOpt = Option(queryRunner.lookupProgramByID(progId)) map {
            _.getDataObject
        }
        // Do a collect for each item or return a set of default values
        dataObjOpt map {
            _.asInstanceOf[SPProgram]
        } map {
            collectValuesFromProgram(_)
        } getOrElse {
            collectNotFoundValues
        }
    }

    /**
     * This method goes through the configuration and if a function is found for
     * the channel value it will call the corresponding function returning a
     * Collected Value
     */
    private def collectValuesFromProgram(program: SPProgram): List[CollectedValue[_]] = {
        configuration flatMap {
            c => new ODBOneValueActor(program, c).collectValues
        }
    }

    private class ODBOneValueActor(program: SPProgram, configuration: GDSConfiguration) extends OneItemKeywordValueActor(configuration) {
        private val LOG = Logger.getLogger(this.getClass.getName)

        override def collectValues(): List[CollectedValue[_]] = {
            val result = extractorFunctions.getOrElse(sourceChannel, unKnownChannelExtractor(_)) (program)
            List(result map (collectedValue) getOrElse (defaultCollectedValue))

//            val readValue = Option(epicsReader.getValue(sourceChannel))
//            try {
//                List(readValue map (collectedValue) getOrElse (defaultCollectedValue))
//            } catch {
//                case e: MatchError => {
//                    LOG.warning("Data for " + fitsKeyword + " keyword was not of the type specified in config file.")
//                    List(ErrorCollectedValue(fitsKeyword, CollectionError.TypeMismatch, fitsComment, headerIndex))
//                }
//            }
        }

        def collectedValue(odbValue: AnyRef): CollectedValue[_] = dataType match {
                case DataType("STRING") => CollectedValue(fitsKeyword, odbValue.toString, fitsComment, headerIndex)
                case DataType("DOUBLE") => odbValue match {
                    case v:java.lang.Number => CollectedValue(fitsKeyword, v.doubleValue(), fitsComment, headerIndex)
                    case _ => ErrorCollectedValue(fitsKeyword, CollectionError.TypeMismatch, fitsComment, headerIndex)
                }
                case DataType("INT") => odbValue match {
                    case v:java.lang.Number => CollectedValue(fitsKeyword, v.intValue(), fitsComment, headerIndex)
                    case _ => ErrorCollectedValue(fitsKeyword, CollectionError.TypeMismatch, fitsComment, headerIndex)
                }
                // this should not happen
                case _ => ErrorCollectedValue(fitsKeyword, CollectionError.TypeMismatch, fitsComment, headerIndex)
            }
    }

    // ExtractorFunction that can read the PI's First Name
    def extractPIFirstName(spProgram: SPProgram) = Option(spProgram.getPIFirstName)

    def extractPILastName(spProgram: SPProgram) = Option(spProgram.getPILastName)

    // Placeholder for queries that cannot be answered, e.g. if the channel is unknown
    def unKnownChannelExtractor(spProgram: SPProgram):Option[AnyRef] = None

    /**
     * This method goes through the configuration and produced an error or a default value for each configuration
     * item
     * This is needed for the case a program is not found in the ODB
     */
    private def collectNotFoundValues: List[CollectedValue[_]] = {
        configuration map { c =>
            if (c.isMandatory) {
                ErrorCollectedValue(c.keyword, CollectionError.MandatoryRequired, c.fitsComment.value, c.index.index)
            } else {
                DefaultCollectedValue(c.keyword, c.nullValue.value, c.fitsComment.value, c.index.index)
            }
        } toList
    }
}