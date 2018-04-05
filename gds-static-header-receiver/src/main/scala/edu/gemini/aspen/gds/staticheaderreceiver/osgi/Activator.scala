package edu.gemini.aspen.gds.staticheaderreceiver.osgi

import java.util

import edu.gemini.aspen.gds.keywords.database.ProgramIdDatabase
import edu.gemini.aspen.gds.staticheaderreceiver.{HeaderReceiver, SeqexecHeaderServlet, TemporarySeqexecKeywordsDatabase, TemporarySeqexecKeywordsDatabaseImpl}
import edu.gemini.util.osgi.Tracker
import org.osgi.framework.{BundleActivator, BundleContext, ServiceRegistration}
import org.osgi.service.event.EventAdmin
import org.osgi.util.tracker.ServiceTracker
import org.ops4j.pax.web.service.WebContainer

class Activator extends BundleActivator {
  var dbRegistration: Option[ServiceRegistration[_]] = None
  var factoryRegistration: Option[ServiceRegistration[_]] = None
  var tracker: Option[ServiceTracker[ProgramIdDatabase, _]] = None

  override def start(context: BundleContext): Unit = {
    val db = new TemporarySeqexecKeywordsDatabaseImpl()
    dbRegistration = Option(context.registerService(classOf[TemporarySeqexecKeywordsDatabase], db: TemporarySeqexecKeywordsDatabase, new util.Hashtable[String, String]()))

    tracker = Option(Tracker.track[ProgramIdDatabase, WebContainer, EventAdmin, ServiceRegistration[_]](context) { (p, w, e) =>
      val service = new SeqexecHeaderServlet(db, p, w, e)
      context.registerService(classOf[HeaderReceiver].getName, service: HeaderReceiver, new util.Hashtable[String, String]())
    } { _.unregister() })
    tracker.foreach(_.open(true))
  }

  override def stop(context: BundleContext): Unit = {
    dbRegistration.foreach(_.unregister())
    dbRegistration = None
    factoryRegistration.foreach(_.unregister())
    factoryRegistration = None
    tracker.foreach(_.close())
    tracker = None
  }
}