package edu.gemini.aspen.gds.observationstate.impl

import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.api.CollectionError
import edu.gemini.aspen.gds.observationstate.{ObservationInfo, ObservationStateConsumer, ObservationStatePublisher}
import org.apache.felix.ipojo.annotations._
import collection.mutable.{SynchronizedSet, HashSet, Set}
import edu.gemini.aspen.gds.api.fits.FitsKeyword

/**
 * Component that publishes Observation state changes
 */
@Component
@Instantiate
@Provides(specifications = Array(classOf[ObservationStatePublisher]))
class ObservationStatePublisherImpl extends ObservationStatePublisher {
  val registeredConsumers: Set[ObservationStateConsumer] = new HashSet[ObservationStateConsumer] with SynchronizedSet[ObservationStateConsumer]

  def publishStartObservation(label: DataLabel) {
    for (consumer <- registeredConsumers) {
      consumer.receiveStartObservation(label)
    }
  }

  override def publishEndObservation(observationInfo: ObservationInfo) {
    registeredConsumers foreach {
      _.receiveEndObservation(observationInfo)
    }
  }

  override def publishObservationError(observationInfo: ObservationInfo) {
    registeredConsumers foreach {
      _.receiveObservationError(observationInfo)
    }
  }

  @Bind(optional = true, aggregate = true)
  def bindConsumer(consumer: ObservationStateConsumer) {
    registeredConsumers += consumer
  }

  @Unbind(optional = true, aggregate = true)
  def unbindConsumer(consumer: ObservationStateConsumer) {
    registeredConsumers -= consumer
  }

}
