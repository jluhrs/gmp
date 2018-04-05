package edu.gemini.aspen.gmp.services.osgi

import java.util

import edu.gemini.aspen.gmp.services.properties.SimplePropertyHolder
import org.osgi.framework.{BundleActivator, BundleContext, ServiceRegistration}
import org.osgi.service.cm.ManagedServiceFactory

class Activator extends BundleActivator {
  var managedServiceFactory: Option[ServiceRegistration[_]] = None
  override def start(context: BundleContext): Unit = {
    val props: util.Hashtable[String, String] = new util.Hashtable()
    props.put("service.pid", classOf[SimplePropertyHolder].getName)
    managedServiceFactory = Option(context.registerService(classOf[ManagedServiceFactory].getName, new SimplePropertyHolderFactory(context), props))
  }

  override def stop(context: BundleContext): Unit = {
    managedServiceFactory.foreach(s => s.unregister())
    managedServiceFactory = None
  }
}