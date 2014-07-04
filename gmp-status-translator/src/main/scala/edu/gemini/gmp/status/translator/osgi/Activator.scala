package edu.gemini.gmp.status.translator.osgi

import org.osgi.framework.{ServiceRegistration, BundleContext, BundleActivator}
import edu.gemini.aspen.giapi.statusservice.StatusHandlerAggregate
import edu.gemini.util.osgi.Tracker._
import edu.gemini.aspen.giapi.status.StatusHandler
import edu.gemini.gmp.top.Top
import edu.gemini.gmp.status.translator.LocalStatusItemTranslator
import scala.collection.JavaConversions._
import org.osgi.service.cm.ManagedServiceFactory
import org.osgi.util.tracker.ServiceTracker

import scalaz._
import Scalaz._

class Activator extends BundleActivator {
  var serviceRegistration: Option[ServiceRegistration[ManagedServiceFactory]] = none
  var topTracker: Option[ServiceTracker[Top, Top]] = none
  var aggregateTracker: Option[ServiceTracker[StatusHandlerAggregate, StatusHandlerAggregate]] = none

  override def start(context: BundleContext) = {
    aggregateTracker = track[StatusHandlerAggregate, StatusHandlerAggregate](context) { aggregate =>
      track[StatusHandler, StatusHandler](context) { s =>
        aggregate.bindStatusHandler(s)
        s
      } { s =>
        aggregate.unbindStatusHandler(s)
      }
      topTracker = track[Top, Top](context) { top =>
        val f = LocalTranslatorFactory(top, aggregate, context)
        serviceRegistration = Some(context.registerService(classOf[ManagedServiceFactory], f, new java.util.Hashtable[String, Any](Map("service.pid" -> classOf[LocalStatusItemTranslator].getName))))
        top
      } { _ =>
        serviceRegistration.foreach(_.unregister())
        serviceRegistration = None
      }.some
      topTracker.foreach(_.open())
      aggregate
    } { aggregate =>
      topTracker.foreach(_.close())
      topTracker = none
    }.some
    aggregateTracker.foreach(_.open())
  }

  override def stop(context: BundleContext) = {
    aggregateTracker.foreach(_.close())
    aggregateTracker = none
  }
}
