package edu.gemini.aspen.gds.errorpolicy

import org.apache.felix.ipojo.annotations.{Provides, Component}
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.api.{ErrorCollectedValue, CollectedValue, DefaultErrorPolicy, ErrorPolicy}

@Component
@Provides
class ErrorsRemovedPolicy extends DefaultErrorPolicy {
    override def applyPolicy(dataLabel: DataLabel, headers: Option[List[CollectedValue[_]]]): Option[List[CollectedValue[_]]] = headers map {
        _ filter {
            _ match {
                case c:ErrorCollectedValue => false
                case c => true
            }
        }
    }
}