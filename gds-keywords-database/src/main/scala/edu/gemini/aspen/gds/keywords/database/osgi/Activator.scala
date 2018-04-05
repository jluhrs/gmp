package edu.gemini.aspen.gds.keywords.database.osgi

import java.util

import edu.gemini.aspen.gds.keywords.database.impl.{KeywordsDatabaseImpl, ProgramIdDatabaseImpl}
import edu.gemini.aspen.gds.keywords.database.{KeywordsDatabase, ProgramIdDatabase}
import org.osgi.framework.{BundleActivator, BundleContext, ServiceRegistration}

class Activator extends BundleActivator {
  var keywordDatabaseRegistration: Option[ServiceRegistration[_]] = None
  var programsDatabaseRegistration: Option[ServiceRegistration[_]] = None

  override def start(context: BundleContext): Unit = {
    keywordDatabaseRegistration = Some(context.registerService(classOf[KeywordsDatabase].getName, new KeywordsDatabaseImpl, new util.Hashtable[String, String]()))
    programsDatabaseRegistration = Some(context.registerService(classOf[ProgramIdDatabase].getName, new ProgramIdDatabaseImpl, new util.Hashtable[String, String]()))
  }

  override def stop(context: BundleContext): Unit = {
    keywordDatabaseRegistration.foreach(_.unregister())
    keywordDatabaseRegistration = None
    programsDatabaseRegistration.foreach(_.unregister())
    programsDatabaseRegistration = None
  }
}