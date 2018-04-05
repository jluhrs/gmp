package edu.gemini.aspen.gmp.epicstostatus.osgi

import java.util.Hashtable

import edu.gemini.util.osgi.Tracker
import org.osgi.framework.{BundleActivator, BundleContext, Constants}
import edu.gemini.epics.EpicsReader
import edu.gemini.jms.api.JmsProvider
import org.osgi.service.cm.ManagedServiceFactory
import org.osgi.util.tracker.ServiceTracker

class Activator extends BundleActivator {
  var tracker: Option[ServiceTracker[EpicsReader, _]] = None

  override def start(bundleContext: BundleContext): Unit = {
    tracker = Some(Tracker.track[EpicsReader, JmsProvider, EpicsToStatusFactory](bundleContext) { (e, p) =>
      val props: Hashtable[String, String] = new Hashtable()
      props.put("service.pid", classOf[EpicsToStatusFactory].getName)
      val cmp = new EpicsToStatusFactory(bundleContext, e, p)
      bundleContext.registerService(classOf[ManagedServiceFactory].getName, cmp, props)
      cmp
    } { _.stopServices() })
    tracker.foreach(_.open)
  }

  override def stop(bundleContext: BundleContext): Unit = {
    tracker.foreach(_.close())
    tracker = None
  }
}