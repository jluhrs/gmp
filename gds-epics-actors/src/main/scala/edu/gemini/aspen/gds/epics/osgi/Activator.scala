package edu.gemini.aspen.gds.epics.osgi

import java.util

import edu.gemini.aspen.gds.api.KeywordActorsFactory
import edu.gemini.aspen.gds.epics.EpicsActorsFactory
import edu.gemini.epics.EpicsReader
import edu.gemini.util.osgi.Tracker
import org.osgi.framework.{BundleActivator, BundleContext, ServiceRegistration}
import org.osgi.util.tracker.ServiceTracker

class Activator extends BundleActivator {
  var factoryRegistration: Option[ServiceRegistration[_]] = None
  var tracker: Option[ServiceTracker[EpicsReader, EpicsActorsFactory]] = None

  override def start(context: BundleContext): Unit = {
    tracker = Option(Tracker.track[EpicsReader, EpicsActorsFactory](context) { p =>
      val factory = new EpicsActorsFactory(p)
      factoryRegistration = Some(context.registerService(classOf[KeywordActorsFactory].getName, factory, new util.Hashtable[String, String]()))
      factory
    } { p =>
      p.unbindAllChannels()
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