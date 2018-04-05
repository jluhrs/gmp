package edu.gemini.aspen.integrationtests.osgi

import edu.gemini.aspen.giapi.status.dispatcher.FilteredStatusHandler
import edu.gemini.aspen.heartbeatdistributor.HeartbeatConsumer
import edu.gemini.aspen.integrationtests.{StatusTranslatorTestHandler, TestConsumerComponent, TestHandler}
import org.osgi.framework.{BundleActivator, BundleContext, ServiceRegistration}

class Activator extends BundleActivator {
  var statusTranslatorService: Option[ServiceRegistration[_]] = None
  var testHandlerService: Option[ServiceRegistration[_]] = None
  var heartbeatService: Option[ServiceRegistration[_]] = None

  override def start(context: BundleContext): Unit = {
    statusTranslatorService = Option(context.registerService(classOf[FilteredStatusHandler].getName, new StatusTranslatorTestHandler, new java.util.Hashtable[String, String]()))
    testHandlerService = Option(context.registerService(classOf[FilteredStatusHandler].getName, new TestHandler(), new java.util.Hashtable[String, String]()))
    heartbeatService = Option(context.registerService(classOf[HeartbeatConsumer].getName, new TestConsumerComponent(1), new java.util.Hashtable[String, String]()))
  }

  override def stop(context: BundleContext): Unit = {
    statusTranslatorService.foreach(_.unregister())
    statusTranslatorService = None
    heartbeatService.foreach(_.unregister())
    heartbeatService = None
    testHandlerService.foreach(_.unregister())
    testHandlerService = None
  }
}