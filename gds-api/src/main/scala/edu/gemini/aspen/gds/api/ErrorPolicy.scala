package edu.gemini.aspen.gds.api

import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.fits.Header
import org.apache.felix.ipojo.annotations.{Provides, Instantiate, Component}

trait ErrorPolicy {
    def applyPolicy(dataLabel:DataLabel, headers: Option[List[Header]]): Option[List[Header]]
}

class DefaultErrorPolicy extends ErrorPolicy {
    override def applyPolicy(dataLabel:DataLabel, headers: Option[List[Header]]): Option[List[Header]] = headers
}

trait CompositeErrorPolicy extends ErrorPolicy

@Component
@Instantiate
@Provides(specifications = Array(classOf[CompositeErrorPolicy]))
class CompositeErrorPolicyImpl extends DefaultErrorPolicy with CompositeErrorPolicy