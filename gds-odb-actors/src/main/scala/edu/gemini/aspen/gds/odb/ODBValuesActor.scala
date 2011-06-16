package edu.gemini.aspen.gds.odb

import edu.gemini.spModel.gemini.obscomp.SPProgram
import edu.gemini.pot.sp.SPProgramID
import edu.gemini.pot.spdb.IDBDatabaseService
import edu.gemini.aspen.gds.api.{DataType, KeywordValueActor, GDSConfiguration, CollectedValue}

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
        // todo: remove the null check
        dataObjOpt.toList map {
            _.asInstanceOf[SPProgram]
        } filter {
            _ != null
        } flatMap {
            collectValuesFromProgram
        }
    }

    /**
     * This method goes through the configuration and if a function is found for
     * the channel value it will call the corresponding function returning a
     * Collected Value
     */
    private def collectValuesFromProgram(program: SPProgram): List[CollectedValue[_]] = {
        for {c <- configuration
             val (fitsKeyword, fitsComment, headerIndex, dataType) = (
                     c.keyword,
                     c.fitsComment.value,
                     c.index.index,
                     c.dataType)
             if extractorFunctions contains c.channel.name
             val result = extractorFunctions(c.channel.name)(program)
             val r = result map { value =>
                 dataType match {
                     case DataType("INT") => CollectedValue(fitsKeyword, value.asInstanceOf[Int], fitsComment, headerIndex)
                     case DataType("DOUBLE") => CollectedValue(fitsKeyword, value.asInstanceOf[Double], fitsComment, headerIndex)
                     case DataType("STRING") => CollectedValue(fitsKeyword, value.asInstanceOf[String], fitsComment, headerIndex)
                 }
             } orElse (defaultCollectedValue(c))
        } yield r.get
    }

    private def newCollectedValue() {

    }

    private def defaultCollectedValue(config: GDSConfiguration): Option[CollectedValue[_]] = if (!config.isMandatory) {
        Option(CollectedValue(config.keyword, config.nullValue.value, config.fitsComment.value, config.index.index))
    } else {
        None
    }

    // ExtractorFunction that can read the PI's First Name
    def extractPIFirstName(spProgram: SPProgram) = Option(spProgram.getPIFirstName)

    def extractPILastName(spProgram: SPProgram) = Option(spProgram.getPILastName)

}