package edu.gemini.aspen.gmp.services.osgi

import java.util

import edu.gemini.aspen.gmp.services.PropertyHolder
import edu.gemini.aspen.gmp.services.jms.JmsService
import edu.gemini.aspen.gmp.services.properties.{PropertyService, SimplePropertyHolder}
import edu.gemini.util.osgi.Tracker
import org.osgi.framework.{BundleActivator, BundleContext, ServiceRegistration}
import org.osgi.service.cm.ManagedServiceFactory
import org.osgi.util.tracker.ServiceTracker

class Activator extends BundleActivator {
  var managedServiceFactory: Option[ServiceRegistration[_]] = None
  var tracker: Option[ServiceTracker[PropertyHolder, JmsService]] = None
  override def start(context: BundleContext): Unit = {
    val props: util.Hashtable[String, String] = new util.Hashtable()
    props.put("service.pid", classOf[SimplePropertyHolder].getName)
    managedServiceFactory = Option(context.registerService(classOf[ManagedServiceFactory].getName, new SimplePropertyHolderFactory(context), props))
    tracker = Option(Tracker.track[PropertyHolder, JmsService](context) { p => new PropertyService(p)} { _ => ()})
    tracker.foreach(_.open)
  }

  override def stop(context: BundleContext): Unit = {
    managedServiceFactory.foreach(s => s.unregister())
    managedServiceFactory = None
    tracker.foreach(_.close())
    tracker = None
  }
}