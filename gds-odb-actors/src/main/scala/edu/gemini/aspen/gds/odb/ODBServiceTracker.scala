package edu.gemini.aspen.gds.odb

import org.apache.felix.ipojo.annotations.{Requires, Instantiate, Component, Validate}
import org.osgi.service.jini.JiniDriver
import net.jini.core.lookup.ServiceTemplate
import edu.gemini.pot.spdb._
import edu.gemini.pot.sp.{SPProgramID, ISPRemoteNode}
import edu.gemini.spModel.gemini.obscomp.SPProgram

/**
 * This is a component that gets a JiniDriver service and uses it to request a reference to
 * an IDBDatabaseService.
 *
 * This way when the IDatabaseService is found it is made available to other services like
 * ODBActorsFactory
 */
@Component
@Instantiate
class ODBServiceTracker(@Requires jiniDriver: JiniDriver) {
    @Validate
    def validate() {
        val st = new ServiceTemplate(null, Array[Class[_]](classOf[IDBDatabaseService]), null)
        val templates = Array[ServiceTemplate](st)
        jiniDriver.setServiceTemplates(templates)
    }

}

@Component
class DBServiceTracker(@Requires dbService: IDBDatabaseService) {
    @Validate
    def validate() {
        val qr = dbService.getQueryRunner
        val functor = qr.queryPrograms(new MyFunctor7("GS-2006B-Q-57")).asInstanceOf[MyFunctor7]
        val piInfo = functor.program.getClientData("DataObject").asInstanceOf[SPProgram].getPIInfo
        println(piInfo.getFirstName + " " + piInfo.getLastName)
    }
}

@serializable class MyFunctor7(programID: String) extends DBAbstractQueryFunctor {
    var program: ISPRemoteNode = null
    def execute(p1: IDBDatabase, p2: ISPRemoteNode) {
        val id = p2.getProgramID()

        // Only look at observations titled "GS-ENG*"
        if (id != null && id.stringValue().equals(programID)) {
            println("Found " + p1 + " " + p2.getProgramID.stringValue)
            program = p2
        }
    }
}