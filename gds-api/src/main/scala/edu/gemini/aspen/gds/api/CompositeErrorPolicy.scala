package edu.gemini.aspen.gds.api

import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.giapi.data.DataLabel
import org.osgi.framework.PackagePermission
import javax.management.remote.rmi._RMIConnection_Stub

/**
 * Interface for an ErrorPolicy that uses OSGi services implementing error policies
 */
trait CompositeErrorPolicy extends ErrorPolicy

/**
 * OSGi service implementing the CompositeErrorPolicy that can use delegates
 */
@Component
@Instantiate
@Provides(specifications = Array(classOf[CompositeErrorPolicy]))
class CompositeErrorPolicyImpl extends DefaultErrorPolicy with CompositeErrorPolicy {
    var policies: List[ErrorPolicy] = List()

    // Let all the original headers to be applied
    override def applyPolicy(dataLabel: DataLabel, headers: Option[List[CollectedValue[_]]]): Option[List[CollectedValue[_]]] = {
        val p = for {ep <- policies
        } yield (ep.applyPolicy(dataLabel, headers))
        println(p)
        Option(p flatMap {
            _.get
        })
    }

    @Bind(optional = true)
    def bindPolicy(ep: ErrorPolicy) {
        policies = ep :: policies
    }

    @Validate
    def validate() {
        println("Validated")
    }
}