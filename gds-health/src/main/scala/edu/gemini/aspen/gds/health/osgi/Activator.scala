package edu.gemini.aspen.gds.health.osgi

import java.util

import edu.gemini.aspen.gds.api.{GDSObseventHandler, KeywordActorsFactory}
import edu.gemini.aspen.gds.health.GdsHealth
import edu.gemini.aspen.gds.staticheaderreceiver.HeaderReceiver
import edu.gemini.aspen.giapi.status.setter.StatusSetter
import edu.gemini.gmp.top.Top
import edu.gemini.jms.api.JmsArtifact
import edu.gemini.util.osgi.Tracker
import org.osgi.framework.{BundleActivator, BundleContext, ServiceRegistration}
import org.osgi.util.tracker.ServiceTracker

class Activator extends BundleActivator {
  var factoryRegistration: Option[ServiceRegistration[_]] = None
  var tracker: Option[ServiceTracker[Top, _]] = None
  var headerReceiverTracker: Option[ServiceTracker[HeaderReceiver, HeaderReceiver]] = None
  var gdsObsEventTracker: Option[ServiceTracker[GDSObseventHandler, GDSObseventHandler]] = None
  var actorFactoryTracker: Option[ServiceTracker[KeywordActorsFactory, KeywordActorsFactory]] = None

  override def start(context: BundleContext): Unit = {
    tracker = Option(Tracker.track[Top, StatusSetter, GdsHealth](context) { (t, s) =>
      val service = new GdsHealth(t, s)
      factoryRegistration = Some(context.registerService(classOf[JmsArtifact].getName, service, new util.Hashtable[String, String]()))
      headerReceiverTracker = Option(Tracker.track[HeaderReceiver, HeaderReceiver](context) { r =>
        service.bindHeaderReceiver()
        r
      } { _ => service.unbindHeaderReceiver() })
      headerReceiverTracker.foreach(_.open(true))
      gdsObsEventTracker = Option(Tracker.track[GDSObseventHandler, GDSObseventHandler](context) { r =>
        service.bindGDSObseventHandler(r)
        r
      } { r => service.unbindGDSObseventHandler(r) })
      gdsObsEventTracker.foreach(_.open(true))
      actorFactoryTracker = Option(Tracker.track[KeywordActorsFactory, KeywordActorsFactory](context) { r =>
        service.bindActorFactory(r)
        r
      } { r => service.unbindActorFactory(r) })
      actorFactoryTracker.foreach(_.open(true))
      service
    } { _ =>
      headerReceiverTracker.foreach(_.close())
      headerReceiverTracker = None
      gdsObsEventTracker.foreach(_.close())
      gdsObsEventTracker = None
      actorFactoryTracker.foreach(_.close())
      actorFactoryTracker = None
    })
    tracker.foreach(_.open(true))
  }

  override def stop(context: BundleContext): Unit = {
    factoryRegistration.foreach(_.unregister())
    factoryRegistration = None
    tracker.foreach(_.close())
    tracker = None
  }
}