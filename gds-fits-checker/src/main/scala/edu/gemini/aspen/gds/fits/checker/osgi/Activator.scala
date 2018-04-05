package edu.gemini.aspen.gds.fits.checker.osgi

import java.util

import edu.gemini.aspen.gds.api.configuration.GDSConfigurationService
import edu.gemini.aspen.gds.fits.checker.InstrumentKeywordsChecker
import edu.gemini.aspen.gds.observationstate.ObservationStateRegistrar
import edu.gemini.aspen.giapi.data.ObservationEventHandler
import edu.gemini.aspen.gmp.services.PropertyHolder
import edu.gemini.util.osgi.Tracker
import org.osgi.framework.{BundleActivator, BundleContext, ServiceRegistration}
import org.osgi.util.tracker.ServiceTracker

class Activator extends BundleActivator {
  var factoryRegistration: Option[ServiceRegistration[_]] = None
  var tracker: Option[ServiceTracker[GDSConfigurationService, _]] = None

  override def start(context: BundleContext): Unit = {
    tracker = Option(Tracker.track[GDSConfigurationService, ObservationStateRegistrar, PropertyHolder, ObservationEventHandler](context) { (c, o, p) =>
      val service = new InstrumentKeywordsChecker(c, o, p)
      factoryRegistration = Some(context.registerService(classOf[ObservationEventHandler].getName, service, new util.Hashtable[String, String]()))
      service
    } { _ => })
    tracker.foreach(_.open(true))
  }

  override def stop(context: BundleContext): Unit = {
    factoryRegistration.foreach(_.unregister())
    factoryRegistration = None
    tracker.foreach(_.close())
    tracker = None
  }
}