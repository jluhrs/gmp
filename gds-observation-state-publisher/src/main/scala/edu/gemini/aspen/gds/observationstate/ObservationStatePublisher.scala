package edu.gemini.aspen.gds.observationstate

import edu.gemini.aspen.giapi.data.DataLabel


/**
 * Trait to be required by ObservationStateImpl to publish observation state changes
 */
trait ObservationStatePublisher {
  /**
   * Call receiveStartObservation on all registered ObservationStateConsumers
   */
  def publishStartObservation(label: DataLabel): Unit

  /**
   * Call receiveEndObservation on all registered ObservationStateConsumers
   */
  //def publishEndObservation(label: DataLabel, missingKeywords: Traversable[FitsKeyword], errorKeywords: Traversable[(FitsKeyword, CollectionError.CollectionError)]): Unit
  def publishEndObservation(observationInfo: ObservationInfo): Unit

  /**
   * Call receiveObservationError on all registered ObservationStateConsumers
   */
  def publishObservationError(observationInfo: ObservationInfo): Unit
}