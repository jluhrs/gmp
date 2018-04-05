package edu.gemini.aspen.gmp.status.simulator.osgi

import java.util

import edu.gemini.aspen.gmp.status.simulator.StatusSimulator
import edu.gemini.gmp.top.Top
import edu.gemini.util.osgi.Tracker
import org.osgi.framework.{BundleActivator, BundleContext, ServiceRegistration}
import org.osgi.service.cm.ManagedServiceFactory
import org.osgi.util.tracker.ServiceTracker

class Activator extends BundleActivator {
  var trackerTop: Option[ServiceTracker[Top, StatusSimulatorFactory]] = None
  var psService: Option[ServiceRegistration[_]] = None

  override def start(context: BundleContext): Unit = {
    trackerTop = Option(Tracker.track[Top, StatusSimulatorFactory](context) { p =>
      val ps = new StatusSimulatorFactory(context, p)
      val props: util.Hashtable[String, String] = new util.Hashtable()
      props.put("service.pid", classOf[StatusSimulator].getName)
      psService = Option(context.registerService(classOf[ManagedServiceFactory].getName, ps, props))
      ps
    } { p =>
        p.stopServices()
        psService.foreach(_.unregister())
    })
    trackerTop.foreach(_.open)
  }

  override def stop(context: BundleContext): Unit = {
    trackerTop.foreach(_.close())
    trackerTop = None
  }
}