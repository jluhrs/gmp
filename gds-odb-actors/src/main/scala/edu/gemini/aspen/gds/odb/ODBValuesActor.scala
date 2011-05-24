package edu.gemini.aspen.gds.odb

import edu.gemini.aspen.gds.api.KeywordValueActor
import edu.gemini.aspen.gds.api.{GDSConfiguration, CollectedValue}
import edu.gemini.spModel.gemini.obscomp.SPProgram
import edu.gemini.pot.sp.{SPProgramID, ISPProgram, ISPRemoteNode}
import edu.gemini.pot.spdb.{IDBDatabaseService, IDBDatabase, DBAbstractQueryFunctor, IDBQueryRunner}

/**
 * Actor that can produce as a reply of a Collect request a set of CollectedValues obtained from a Query
 * to the ODB. The query is in the functor below and basically consists of retrieving the program
 * for a given programID
 */
class ODBValuesActor(programID:String, queryRunner: IDBDatabaseService, configuration: List[GDSConfiguration]) extends KeywordValueActor {
    type ExtractorFunction = SPProgram => AnyRef
    type CollectorFunction = SPProgram => CollectedValue

    // Defines a list of "channels" to functions map that can extract information from a SPProgram instance
    // More methods can be added to the list to support more channels
    val extractorFunctions = Map[String, ExtractorFunction](
        "odb:piLastName" -> extractPILastName,
        "odb:piFirstName" -> extractPIFirstName)

    start()

    override def collectValues(): List[CollectedValue] = {
        // Do the ODB query
        val progId = SPProgramID.toProgramID(programID)
        val dataObjOpt = Option(queryRunner.lookupProgramByID(progId)) map { _.getDataObject }
        // todo: remove the null check
        dataObjOpt.toList map { _.asInstanceOf[SPProgram]} filter { _ != null} flatMap { collectValuesFromProgram }
    }

    /**
     * This method goes through the configuration and if a function is found for
     * the channel value it will call the corresponding function returning a
     * Collected Value
     */
    private def collectValuesFromProgram(program: SPProgram): List[CollectedValue] = {
        for {c <- configuration
             val (fitsKeyword, fitsComment, headerIndex) = (
                     c.keyword,
                     c.fitsComment.value,
                     c.index.index)
             if extractorFunctions contains c.channel.name
             val r = extractorFunctions(c.channel.name)(program)
        }
        yield CollectedValue(fitsKeyword, r, fitsComment, headerIndex)
    }

    // ExtractorFunction that can read the PI's First Name
    def extractPIFirstName(spProgram: SPProgram) = spProgram.getPIFirstName

    def extractPILastName(spProgram: SPProgram) = spProgram.getPILastName

}