package edu.gemini.aspen.gds.obsevent.handler.osgi

import java.util

import edu.gemini.aspen.gds.actors.factory.CompositeActorsFactory
import edu.gemini.aspen.gds.api.{CompositePostProcessingPolicy, GDSObseventHandler}
import edu.gemini.aspen.gds.keywords.database.KeywordsDatabase
import edu.gemini.aspen.gds.obsevent.handler.{GDSObseventHandlerImpl, ObservationEvent2EventAdmin}
import edu.gemini.aspen.giapi.data.ObservationEventHandler
import edu.gemini.aspen.gmp.services.PropertyHolder
import edu.gemini.util.osgi.Tracker
import org.osgi.framework.{BundleActivator, BundleContext, ServiceRegistration}
import org.osgi.service.event.{EventAdmin, EventConstants, EventHandler}
import org.osgi.util.tracker.ServiceTracker

class Activator extends BundleActivator {
  var tracker: Option[ServiceTracker[CompositeActorsFactory, _]] = None
  var eventAdminTracker: Option[ServiceTracker[EventAdmin, _]] = None

  override def start(context: BundleContext): Unit = {
    val props = new util.Hashtable[String, String]()
    props.put(EventConstants.EVENT_TOPIC, GDSObseventHandler.ObsEventTopic)
    tracker = Option(Tracker.track[CompositeActorsFactory, KeywordsDatabase, CompositePostProcessingPolicy, PropertyHolder, EventAdmin, ServiceRegistration[_]](context) { (a, k, c, p, e) =>
      context.registerService(Array(classOf[GDSObseventHandler].getName, classOf[EventHandler].getName), new GDSObseventHandlerImpl(a, k, c, p, e): GDSObseventHandler, props)
    } { _.unregister() })
    tracker.foreach(_.open(true))

    eventAdminTracker = Option(Tracker.track[EventAdmin, ServiceRegistration[_]](context) { e =>
      context.registerService(classOf[ObservationEventHandler], new ObservationEvent2EventAdmin(e), new util.Hashtable[String, String]())
    } { _.unregister()})
    eventAdminTracker.foreach(_.open(true))
  }

  override def stop(context: BundleContext): Unit = {
    tracker.foreach(_.close())
    tracker = None
    eventAdminTracker.foreach(_.close())
    eventAdminTracker = None
  }
}