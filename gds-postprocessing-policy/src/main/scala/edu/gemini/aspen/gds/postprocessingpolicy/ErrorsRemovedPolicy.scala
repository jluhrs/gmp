package edu.gemini.aspen.gds.postprocessingpolicy

import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.api.{PostProcessingPolicy, ErrorCollectedValue, CollectedValue, DefaultPostProcessingPolicy}
import org.apache.felix.ipojo.annotations.{Instantiate, Provides, Component}

/**
 * This policy removes ErrorCollectedValues. It should probably be the last policy applied.
 */
@Component
//@Instantiate
@Provides(specifications = Array[Class[_]](classOf[PostProcessingPolicy]))
class ErrorsRemovedPolicy extends DefaultPostProcessingPolicy {
  override val priority = 10

  override def applyPolicy(dataLabel: DataLabel, headers: List[CollectedValue[_]]): List[CollectedValue[_]] = headers filter {
    LOG.fine("Remove errors form " + dataLabel)

    _ match {
      case c: ErrorCollectedValue => false
      case c => true
    }
  }

  override def toString = this.getClass.getSimpleName
}