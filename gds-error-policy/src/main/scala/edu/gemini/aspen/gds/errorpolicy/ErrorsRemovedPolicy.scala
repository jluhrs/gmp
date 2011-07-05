package edu.gemini.aspen.gds.errorpolicy

import org.apache.felix.ipojo.annotations.{Provides, Component}
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.api.{ErrorCollectedValue, CollectedValue, DefaultErrorPolicy}

@Component
@Provides
class ErrorsRemovedPolicy extends DefaultErrorPolicy {
    override def applyPolicy(dataLabel: DataLabel, headers: List[CollectedValue[_]]): List[CollectedValue[_]] = headers filter {
        _ match {
            case c: ErrorCollectedValue => false
            case c => true
        }
    }
}