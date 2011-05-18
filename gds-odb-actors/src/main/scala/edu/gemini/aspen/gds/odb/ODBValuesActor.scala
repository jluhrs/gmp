package edu.gemini.aspen.gds.odb

import edu.gemini.aspen.gds.api.KeywordValueActor
import edu.gemini.aspen.gds.api.{GDSConfiguration, CollectedValue}
import edu.gemini.spModel.gemini.obscomp.SPProgram
import edu.gemini.pot.spdb.{IDBDatabase, DBAbstractQueryFunctor, IDBQueryRunner}
import edu.gemini.pot.sp.{ISPProgram, ISPRemoteNode}

/**
 * Actor that can produce as a reply of a Collect request a set of CollectedValues obtained from a Query
 * to the ODB. The query is in the functor below and basically consists of retrieving the program
 * for a given programID
 */
class ODBValuesActor(programID:String, queryRunner: IDBQueryRunner, configuration: List[GDSConfiguration]) extends KeywordValueActor {
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
        val functor = queryRunner.queryPrograms(GDSProgramQuery(programID)).asInstanceOf[GDSProgramQuery]
        functor.program match {
            case Some(program) => collectValuesFromProgram(program)
            case None => List()
        }
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
             val r = extractorFunctions.getOrElse(c.channel.name, null)(program)
        }
        yield CollectedValue(fitsKeyword, r, fitsComment, headerIndex)
    }

    // ExtractorFunction that can read the PI's First Name
    def extractPIFirstName(spProgram: SPProgram) = spProgram.getPIFirstName

    def extractPILastName(spProgram: SPProgram) = spProgram.getPILastName

}

/**
 * This class is used by the ODBValuesActor though the actual implementation is in the next
 * class which is instantiated by the GDSProgramQuery companion object
 */
abstract class GDSProgramQuery(programID: String) extends DBAbstractQueryFunctor {
    def program: Option[SPProgram]
}

/**
 * This is the actual functor. It is made to extend GDSProgramQuery to isolate the class
 * name from the rest of the code, making it easier to change the classname without
 * impacting other parts
 *
 * Changing the code may be required when changing the functor code which is cached on the ODB
 */
class GetProgramQuery6(programID: String) extends GDSProgramQuery(programID) {
    var program: Option[SPProgram] = None

    def execute(p1: IDBDatabase, p2: ISPRemoteNode) {
        val currentProgramID = p2.getProgramID

        if (currentProgramID != null && currentProgramID.stringValue().equals(programID) && p2.isInstanceOf[ISPProgram]) {
            val ispProgram = p2.asInstanceOf[ISPProgram]
            val dataObject = Option(ispProgram.getDataObject)
            dataObject match {
                case Some(spProgram:SPProgram) => program = Option(spProgram)
                case Some(x) =>
                case None =>
            }
        }
    }
}

/**
 * Acts as a factory class of GDSProgramQuery objects
 */
object GDSProgramQuery {
    def apply(programID: String): GDSProgramQuery = {
        new GetProgramQuery6(programID)
    }
}