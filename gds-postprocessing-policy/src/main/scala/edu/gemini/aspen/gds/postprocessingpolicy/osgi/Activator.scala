package edu.gemini.aspen.gds.postprocessingpolicy.osgi

import java.util

import edu.gemini.aspen.gds.api.PostProcessingPolicy
import edu.gemini.aspen.gds.api.configuration.GDSConfigurationService
import edu.gemini.aspen.gds.postprocessingpolicy._
import edu.gemini.util.osgi.Tracker
import org.osgi.framework.{BundleActivator, BundleContext, ServiceRegistration}
import org.osgi.service.cm.ManagedServiceFactory
import org.osgi.util.tracker.ServiceTracker

class Activator extends BundleActivator {
  var deleteOriginalRegistration: Option[ServiceRegistration[_]] = None
  var enforceMandatoryRegistration: Option[ServiceRegistration[_]] = None
  var setOwnershipFactoryRegistration: Option[ServiceRegistration[_]] = None
  var setPermissionsFactoryRegistration: Option[ServiceRegistration[_]] = None
  var tracker: Option[ServiceTracker[GDSConfigurationService, List[ServiceRegistration[_]]]] = None

  override def start(context: BundleContext): Unit = {
    val deleteOriginalPolicy = new DeleteOriginalPolicy()
    deleteOriginalRegistration = Some(context.registerService(classOf[PostProcessingPolicy].getName, deleteOriginalPolicy, new util.Hashtable[String, String]()))

    val soFactory = new SetOwnershipPolicyFactory(context)
    val soProps: util.Hashtable[String, String] = new util.Hashtable()
    soProps.put("service.pid", classOf[SetOwnershipPolicy].getName)
    setOwnershipFactoryRegistration = Option(context.registerService(classOf[ManagedServiceFactory].getName, soFactory, soProps))

    val spFactory = new SetPermissionsPolicyFactory(context)
    val spProps: util.Hashtable[String, String] = new util.Hashtable()
    spProps.put("service.pid", classOf[SetPermissionsPolicy].getName)
    setPermissionsFactoryRegistration = Option(context.registerService(classOf[ManagedServiceFactory].getName, spFactory, soProps))

    tracker = Option(Tracker.track[GDSConfigurationService, List[ServiceRegistration[_]]](context) { p =>
      val enforceMandatoryPolicy = context.registerService(classOf[PostProcessingPolicy], new EnforceMandatoryPolicy(p), new util.Hashtable[String, String]())
      val enforceOrderPolicy = context.registerService(classOf[PostProcessingPolicy], new EnforceOrderPolicy(p), new util.Hashtable[String, String]())
      List(enforceMandatoryPolicy, enforceOrderPolicy)
    } { _.foreach(_.unregister()) })
    tracker.foreach(_.open(true))
  }

  override def stop(context: BundleContext): Unit = {
    deleteOriginalRegistration.foreach(_.unregister())
    deleteOriginalRegistration = None
    tracker.foreach(_.close())
    tracker = None
    setOwnershipFactoryRegistration.foreach(_.unregister())
    setOwnershipFactoryRegistration = None
    setPermissionsFactoryRegistration.foreach(_.unregister())
    setPermissionsFactoryRegistration = None
  }
}