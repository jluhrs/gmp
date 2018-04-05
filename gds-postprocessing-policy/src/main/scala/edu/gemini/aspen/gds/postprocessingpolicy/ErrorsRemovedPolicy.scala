package edu.gemini.aspen.gds.postprocessingpolicy

import edu.gemini.aspen.gds.api.{CollectedValue, DefaultPostProcessingPolicy, ErrorCollectedValue}
import edu.gemini.aspen.giapi.data.DataLabel

/**
 * This policy removes ErrorCollectedValues. It should probably be the last policy applied.
 */
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