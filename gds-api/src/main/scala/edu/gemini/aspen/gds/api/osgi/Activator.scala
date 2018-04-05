package edu.gemini.aspen.gds.api.osgi

import java.util

import edu.gemini.aspen.gds.api.configuration.GDSConfigurationService
import edu.gemini.aspen.gds.api.{CompositePostProcessingPolicy, CompositePostProcessingPolicyImpl, PostProcessingPolicy}
import edu.gemini.util.osgi.Tracker
import org.osgi.framework.{BundleActivator, BundleContext, ServiceRegistration}
import org.osgi.service.cm.ManagedServiceFactory
import org.osgi.util.tracker.ServiceTracker

class Activator extends BundleActivator {
  var tracker: Option[ServiceTracker[PostProcessingPolicy, PostProcessingPolicy]] = None
  var policyService: Option[ServiceRegistration[_]] = None
  var configService: Option[ServiceRegistration[_]] = None

  override def start(context: BundleContext): Unit = {
    val props: util.Hashtable[String, String] = new util.Hashtable()
    props.put("service.pid", classOf[GDSConfigurationService].getName)
    configService = Some(context.registerService(classOf[ManagedServiceFactory].getName, new GDSConfigurationServiceFactory(context), props))
    val composite = new CompositePostProcessingPolicyImpl()
    policyService = Some(context.registerService(classOf[CompositePostProcessingPolicy].getName, composite, new util.Hashtable[String, String]()))
    tracker = Option(Tracker.track[PostProcessingPolicy, PostProcessingPolicy](context) { p =>
      composite.addPolicy(p)
      p
    } { p =>
        composite.removePolicy(p)
    })
    tracker.foreach(_.open)
  }

  override def stop(context: BundleContext): Unit = {
    tracker.foreach(_.close())
    tracker = None
    policyService.foreach(_.unregister())
    policyService = None
    configService.foreach(_.unregister())
    configService = None
  }
}