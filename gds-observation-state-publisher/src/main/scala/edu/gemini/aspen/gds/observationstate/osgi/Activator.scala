package edu.gemini.aspen.gds.observationstate.osgi

import java.util

import edu.gemini.aspen.gds.api.configuration.GDSConfigurationService
import edu.gemini.aspen.gds.api.{GDSNotification, PostProcessingPolicy}
import edu.gemini.aspen.gds.observationstate.impl.{GDSEventsListener, InspectPolicy, ObservationStateImpl, ObservationStatePublisherImpl}
import edu.gemini.aspen.gds.observationstate.{ObservationStateConsumer, ObservationStateProvider, ObservationStateRegistrar}
import edu.gemini.util.osgi.Tracker
import org.osgi.framework.{BundleActivator, BundleContext, ServiceRegistration}
import org.osgi.service.event.{EventConstants, EventHandler}
import org.osgi.util.tracker.ServiceTracker

class Activator extends BundleActivator {
  var stateConsumerTracker: Option[ServiceTracker[ObservationStateConsumer, _]] = None
  var statePublisherRegistration: Option[ServiceRegistration[_]] = None
  var listenerRegistration: Option[ServiceRegistration[EventHandler]] = None
  var configurationTracker: Option[ServiceTracker[GDSConfigurationService, ServiceRegistration[_]]] = None

  override def start(context: BundleContext): Unit = {
    val publisher = new ObservationStatePublisherImpl()
    stateConsumerTracker = Option(Tracker.track[ObservationStateConsumer, ObservationStateConsumer](context) { c =>
      publisher.bindConsumer(c)
      c
    } {c =>
      publisher.unbindConsumer(c)
    })
    stateConsumerTracker.foreach(_.open(true))

    val obsStateProvider = new ObservationStateImpl(publisher)
    statePublisherRegistration = Option(context.registerService(Array(classOf[ObservationStateRegistrar], classOf[ObservationStateProvider]).map(_.getName), obsStateProvider, new util.Hashtable[String, String]()))

    val props = new util.Hashtable[String, String]()
    props.put(EventConstants.EVENT_TOPIC, GDSNotification.GDSEventsTopic)
    listenerRegistration = Option(context.registerService(classOf[EventHandler], new GDSEventsListener(obsStateProvider): EventHandler, props))

    configurationTracker = Option(Tracker.track[GDSConfigurationService, ServiceRegistration[_]](context) { e =>
      context.registerService(classOf[PostProcessingPolicy], new InspectPolicy(e, obsStateProvider): PostProcessingPolicy, new util.Hashtable[String, String]())
    } { _.unregister()})
    configurationTracker.foreach(_.open(true))
  }

  override def stop(context: BundleContext): Unit = {
    statePublisherRegistration.foreach(_.unregister())
    statePublisherRegistration = None
    stateConsumerTracker.foreach(_.close())
    stateConsumerTracker = None
    listenerRegistration.foreach(_.unregister())
    listenerRegistration = None
    configurationTracker.foreach(_.close())
    configurationTracker = None
  }
}