package edu.gemini.gpi.observationstatus.osgi

import edu.gemini.aspen.giapi.status.StatusHandler
import edu.gemini.aspen.giapi.status.setter.StatusSetter
import edu.gemini.gmp.top.Top
import edu.gemini.gpi.observationstatus.GpiItemTranslator
import edu.gemini.jms.api.JmsArtifact
import org.osgi.framework.{ServiceRegistration, BundleContext, BundleActivator}
import edu.gemini.util.osgi.Tracker._
import org.osgi.util.tracker.ServiceTracker

class Activator extends BundleActivator {

  var translatorRegistration:Option[ServiceRegistration[_]] = None

  var gmpTranslatorTracker: Option[ServiceTracker[_, _]] = None

  override def start(context: BundleContext) = {
    gmpTranslatorTracker = Some(track[Top, StatusSetter, StatusHandler](context) { (top, ss) =>
        val translator = new GpiItemTranslator(top, ss)
        translatorRegistration = Some(context.registerService(Array[String](classOf[StatusHandler].getName, classOf[JmsArtifact].getName), translator, new java.util.Hashtable[String, String]()))
        translator
      } { t =>

      })
    gmpTranslatorTracker.foreach(_.open())
  }

  override def stop(context: BundleContext) = {
    translatorRegistration.foreach(_.unregister())
    translatorRegistration = None

    gmpTranslatorTracker.foreach(_.close())
    gmpTranslatorTracker = None
  }
}
