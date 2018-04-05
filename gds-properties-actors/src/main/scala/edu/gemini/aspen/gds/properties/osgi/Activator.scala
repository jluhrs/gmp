package edu.gemini.aspen.gds.properties.osgi

import java.util

import edu.gemini.aspen.gds.api.KeywordActorsFactory
import edu.gemini.aspen.gds.properties.PropertiesActorsFactory
import org.osgi.framework.{BundleActivator, BundleContext, ServiceRegistration}

class Activator extends BundleActivator {
  var factoryRegistration: Option[ServiceRegistration[_]] = None

  override def start(context: BundleContext): Unit = {
    val factory = new PropertiesActorsFactory()
    factoryRegistration = Some(context.registerService(classOf[KeywordActorsFactory].getName, factory, new util.Hashtable[String, String]()))
  }

  override def stop(context: BundleContext): Unit = {
    factoryRegistration.foreach(_.unregister())
    factoryRegistration = None
  }
}