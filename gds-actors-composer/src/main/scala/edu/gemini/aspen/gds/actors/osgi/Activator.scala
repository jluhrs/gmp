package edu.gemini.aspen.gds.actors.osgi

import java.util

import edu.gemini.aspen.gds.actors.factory.{CompositeActorsFactory, CompositeActorsFactoryImpl}
import edu.gemini.aspen.gds.api.configuration.GDSConfigurationService
import edu.gemini.aspen.gds.api.KeywordActorsFactory
import edu.gemini.util.osgi.Tracker
import org.osgi.framework.{BundleActivator, BundleContext, ServiceRegistration}
import org.osgi.util.tracker.ServiceTracker

class Activator extends BundleActivator {
  var actorsTracker: Option[ServiceTracker[KeywordActorsFactory, KeywordActorsFactory]] = None
  var configurationTracker: Option[ServiceTracker[GDSConfigurationService, CompositeActorsFactoryImpl]] = None
  var compositeRegistration: Option[ServiceRegistration[_]] = None

  override def start(context: BundleContext): Unit = {
    configurationTracker = Option(Tracker.track[GDSConfigurationService, CompositeActorsFactoryImpl](context) { p =>
      val composite = new CompositeActorsFactoryImpl(p)
      compositeRegistration = Some(context.registerService(classOf[CompositeActorsFactory].getName, composite, new util.Hashtable[String, String]()))
      actorsTracker = Option(Tracker.track[KeywordActorsFactory, KeywordActorsFactory](context) { p =>
        composite.addFactory(p)
        p
      } { p =>
        composite.removeFactory(p)
      })
      actorsTracker.foreach(_.open)
      composite
    } { _ => })
    configurationTracker.foreach(_.open(true))
  }

  override def stop(context: BundleContext): Unit = {
    configurationTracker.foreach(_.close())
    configurationTracker = None
    actorsTracker.foreach(_.close())
    actorsTracker = None
    compositeRegistration.foreach(_.unregister())
    compositeRegistration = None
  }
}