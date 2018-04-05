package edu.gemini.aspen.gds.api.osgi

import java.util

import edu.gemini.aspen.gds.api.{CompositePostProcessingPolicy, CompositePostProcessingPolicyImpl, PostProcessingPolicy}
import edu.gemini.util.osgi.Tracker
import org.osgi.framework.{BundleActivator, BundleContext, ServiceRegistration}
import org.osgi.util.tracker.ServiceTracker

class Activator extends BundleActivator {
  var trackerTop: Option[ServiceTracker[PostProcessingPolicy, PostProcessingPolicy]] = None
  var psService: Option[ServiceRegistration[_]] = None

  override def start(context: BundleContext): Unit = {
    val composite = new CompositePostProcessingPolicyImpl()
    psService = Some(context.registerService(classOf[CompositePostProcessingPolicy].getName, composite, new util.Hashtable[String, String]()))
    trackerTop = Option(Tracker.track[PostProcessingPolicy, PostProcessingPolicy](context) { p =>
      composite.addPolicy(p)
      p
    } { p =>
        composite.removePolicy(p)
    })
    trackerTop.foreach(_.open)
  }

  override def stop(context: BundleContext): Unit = {
    trackerTop.foreach(_.close())
    trackerTop = None
    psService.foreach(_.unregister())
    psService = None
  }
}