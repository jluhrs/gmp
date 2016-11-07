package edu.gemini.gpi.observationstatus.osgi

import edu.gemini.aspen.giapi.status.StatusHandler
import edu.gemini.aspen.giapi.status.setter.StatusSetter
import edu.gemini.epics.api.EpicsClient
import edu.gemini.epics.impl.EpicsObserverImpl
import edu.gemini.epics.{EpicsObserver, JCAContextController}
import edu.gemini.gmp.top.Top
import edu.gemini.gpi.observationstatus.{GpiItemTranslator, MassDimmTimetracker}
import edu.gemini.jms.api.JmsArtifact
import edu.gemini.util.osgi.Tracker._
import org.osgi.framework.{BundleActivator, BundleContext, ServiceRegistration}
import org.osgi.util.tracker.ServiceTracker

import scala.collection.JavaConverters._

class Activator extends BundleActivator {

  var translatorRegistration:Option[ServiceRegistration[_]] = None

  var gmpTranslatorTracker: Option[ServiceTracker[_, _]] = None

  var jcaContextTracker: Option[ServiceTracker[_, _]] = None

  var epicsClient: Option[EpicsClient] = None

  override def start(context: BundleContext) = {
    gmpTranslatorTracker = Some(track[Top, StatusSetter, StatusHandler](context) { (top, ss) =>
        val translator = new GpiItemTranslator(top, ss)
        translatorRegistration = Some(context.registerService(Array[String](classOf[StatusHandler].getName, classOf[JmsArtifact].getName), translator, new java.util.Hashtable[String, String]()))
        translator
      } { t =>

      })
    gmpTranslatorTracker.foreach(_.open())

    jcaContextTracker = Some(track[Top, StatusSetter, JCAContextController, EpicsObserver](context) { (top, ss, jca) =>
        val observer = new EpicsObserverImpl(jca)

        val massDimmTracker = new MassDimmTimetracker(top, ss)
        epicsClient = Some(massDimmTracker)

        observer.registerEpicsClient(massDimmTracker, List(MassDimmTimetracker.massEpicsChannel, MassDimmTimetracker.dimmEpicsChannel).asJavaCollection)
        observer
      } { t =>
        epicsClient.foreach(t.unregisterEpicsClient)
        epicsClient = None
      })
    jcaContextTracker.foreach(_.open())
  }

  override def stop(context: BundleContext) = {
    translatorRegistration.foreach(_.unregister())
    translatorRegistration = None

    gmpTranslatorTracker.foreach(_.close())
    gmpTranslatorTracker = None

    jcaContextTracker.foreach(_.close())
    jcaContextTracker = None

    epicsClient = None
  }
}
