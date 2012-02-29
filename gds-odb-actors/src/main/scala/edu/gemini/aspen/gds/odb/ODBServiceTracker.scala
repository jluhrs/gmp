package edu.gemini.aspen.gds.odb

import edu.gemini.pot.spdb.IDBDatabaseService
import org.apache.felix.ipojo.annotations._
import org.osgi.service.jini.JiniDriver
import net.jini.core.lookup.ServiceTemplate

/**
 * This is a component that gets a JiniDriver service and uses it to request a reference to
 * an IDBDatabaseService.
 *
 * This way when the IDatabaseService is found it is made available to other services like
 * ODBActorsFactory
 *
 * It requires that the JiniDriver be available and configured in the OSGi container
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