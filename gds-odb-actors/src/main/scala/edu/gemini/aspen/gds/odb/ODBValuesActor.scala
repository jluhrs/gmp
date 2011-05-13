package edu.gemini.aspen.gds.odb

import edu.gemini.aspen.gds.api.KeywordValueActor
import edu.gemini.aspen.gds.api.{GDSConfiguration, CollectedValue}
import edu.gemini.spModel.gemini.obscomp.SPProgram
import edu.gemini.pot.sp.ISPRemoteNode
import edu.gemini.pot.spdb.{IDBDatabase, DBAbstractQueryFunctor, IDBQueryRunner}

/**
 * Actor that can produce as a reply of a Collect request with the value of an StatusItem linked
 * to a single fitsKeyword
 */
class ODBValuesActor(queryRunner: IDBQueryRunner, configuration: List[GDSConfiguration]) extends KeywordValueActor {
    type ExtractorFunction = SPProgram => AnyRef
    type CollectorFunction = SPProgram => CollectedValue

    val extractorFunctions = Map[String, ExtractorFunction](
        "odb:piFirstName" -> extractPIFirstName)

    override def collectValues(): List[CollectedValue] = {
        val functor = queryRunner.queryPrograms(GDSProgramQuery("GS-2006B-Q-57")).asInstanceOf[GDSProgramQuery]
        val spProgram = functor.program
        spProgram match {
            case Some(program) => {
                //for (extractor <- buildExtractors) yield extractor(program)
                for {c <- configuration
                     val (fitsKeyword, fitsComment, headerIndex) = (
                             c.keyword,
                             c.fitsComment.value,
                             c.index.index)
                     val r = extractorFunctions.getOrElse(c.channel.name, null)(program)
                //val col -
                }
                yield CollectedValue(fitsKeyword, r, fitsComment, headerIndex)
            }
            case None => List()
        }
    }

    //    def buildExtractors: List[CollectorFunction] = {
    //        configuration map {
    //            c => def extractor(spProgram:SPProgram):CollectedValue = {
    //                val (fitsKeyword, fitsComment, headerIndex) = (
    //                c.keyword,
    //                c.fitsComment.value,
    //                c.index.index)
    //                val extractor = extractorFunctions getOrElse (c.channel.name, null)
    //                val value = extractor(spProgram)
    //                CollectedValue(fitsKeyword, value, fitsComment, headerIndex)
    //                extractor
    //            }
    //
    //
    //        }
    //        //List(extractPIFirstName)
    //    }


    //    def extractPIFirstName(configuration: GDSConfiguration, extractor:ExtractorFunction): ExtractorFunction = {

    //    def subExtractFunction(configuration: GDSConfiguration, extractor:ExtractorFunction): ExtractorFunction = {
    //        val (fitsKeyword, fitsComment, headerIndex) = (
    //                configuration.keyword,
    //                configuration.fitsComment.value,
    //                configuration.index.index)
    //        val value = extractor(spProgram)
    //        CollectedValue(fitsKeyword, value, fitsComment, headerIndex)
    //    }

    def extractPIFirstName(spProgram: SPProgram) = spProgram.getPIFirstName

}

abstract class GDSProgramQuery(programID: String) extends DBAbstractQueryFunctor {
   def program: Option[SPProgram]
}

/**
 * This is the actual functor. It is made to extend GDSProgramQuery to isolate the class
 * name from the rest of the code. making it quite easy to change the classname without
 * impacting other parts
 *
 * Changing the code may be required when changing the functor code which is cached on the ODB
 */
class GDSProgramQuery1(programID: String) extends GDSProgramQuery(programID) {
    var program: Option[SPProgram]= None

    def execute(p1: IDBDatabase, p2: ISPRemoteNode) {
        val programID = p2.getProgramID()

        if (programID != null && programID.stringValue().equals(programID)) {
            program = Option(p2.asInstanceOf[SPProgram])
        }
    }
}

object GDSProgramQuery {
    def apply(programID: String): GDSProgramQuery = {
        new GDSProgramQuery1(programID)
    }
}