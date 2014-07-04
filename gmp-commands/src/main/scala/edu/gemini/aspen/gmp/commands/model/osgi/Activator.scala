package edu.gemini.aspen.gmp.commands.model.osgi

import java.util.Dictionary
import java.util.logging.Logger

import edu.gemini.aspen.giapi.commands.{CommandSender, CommandUpdater}
import edu.gemini.aspen.giapi.status.setter.StatusSetter
import edu.gemini.aspen.gmp.commands.handlers.CommandHandlers
import edu.gemini.aspen.gmp.commands.model.{ActionSender, Action, ActionMessageBuilder}
import edu.gemini.aspen.gmp.commands.model.executors.{SequenceCommandExecutorStrategy, SequenceCommandExecutor}
import edu.gemini.aspen.gmp.commands.model.impl.{CommandSenderImpl, ActionManager, ActionManagerImpl, CommandUpdaterImpl}
import edu.gemini.gmp.top.Top
import org.osgi.framework.BundleActivator
import org.osgi.framework.BundleContext
import org.osgi.framework.ServiceRegistration

import edu.gemini.util.osgi.Tracker._
import org.osgi.service.cm.ManagedServiceFactory
import org.osgi.util.tracker.ServiceTracker

import scala.collection.mutable
import scalaz._
import Scalaz._

class Activator extends BundleActivator {

  var amRegistration: Option[ServiceRegistration[ActionManager]] = none
  var cuRegistration: Option[ServiceRegistration[CommandUpdater]] = none
  var csRegistration: Option[ServiceRegistration[CommandSender]] = none
  var factoryTracker: Option[ServiceTracker[ActionMessageBuilder, _]] = none
  var senderTracker: Option[ServiceTracker[ActionSender, _]] = none
  var actionManager: Option[ActionManagerImpl] = none

  def start(context: BundleContext) {
    val am = new ActionManagerImpl
    am.start()
    actionManager = am.some
    amRegistration = context.registerService(classOf[ActionManager], am, new java.util.Hashtable[String, String]).some
    val commandUpdater = new CommandUpdaterImpl(am)
    cuRegistration = context.registerService(classOf[CommandUpdater], commandUpdater, new java.util.Hashtable[String, String]).some

    factoryTracker = track[ActionMessageBuilder, CommandHandlers, StatusSetter, Top, SequenceCommandExecutorFactory](context) { (b, ch, ss, t) =>
        val f = SequenceCommandExecutorFactory(b, am, ch, ss, t, context)
        val props = new java.util.Hashtable[String, String]()
        props.put("service.pid", classOf[SequenceCommandExecutorStrategy].getName)
        context.registerService(classOf[ManagedServiceFactory], f, props).some
        f
      } { s =>
      }.some
    factoryTracker.foreach(_.open())

    senderTracker = track[ActionSender, SequenceCommandExecutor, CommandSender](context) { (s, e) =>
        val f = new CommandSenderImpl(am, s, e)
        csRegistration = context.registerService(classOf[CommandSender], f, new java.util.Hashtable[String, String]).some
        f
      } { s =>
        csRegistration.foreach(_.unregister())
        csRegistration = none
      }.some
    senderTracker.foreach(_.open())
  }

  def stop(context: BundleContext) {
    (cuRegistration :: amRegistration :: csRegistration :: Nil).foreach(_.map(_.unregister))
    cuRegistration = none
    amRegistration = none
    csRegistration = none
    (factoryTracker :: senderTracker :: Nil).foreach(_.map(_.close()))
    factoryTracker = none
    senderTracker = none
    actionManager.foreach(_.stop)
    actionManager = none
  }

}

case class SequenceCommandExecutorFactory(builder: ActionMessageBuilder, manager: ActionManager, handler: CommandHandlers, setter:StatusSetter, top: Top, context: BundleContext) extends ManagedServiceFactory {
  final val STARTUP_SCRIPT: String = "instrumentStartupScript"
  private final val LOG = Logger.getLogger(classOf[SequenceCommandExecutorFactory].getName)
  private final val existingServices: mutable.Map[String, ServiceRegistration[_]] = mutable.Map.empty

  def getName = "SequenceCommandExecutorFactory factory"

  override def updated(pid: String, properties: Dictionary[String, _]) =
    for {
      configName <- Option(properties.get(STARTUP_SCRIPT))
    } yield registerExecutor(pid, configName.toString)

  private def registerExecutor(pid: String, startup: String) = {
    deleted(pid)
    val executor = new SequenceCommandExecutorStrategy(builder, manager, handler, setter, top, startup)
    try {
      val serviceRegistration = context.registerService(classOf[SequenceCommandExecutor], executor, new java.util.Hashtable[String, AnyRef])
      existingServices += pid -> serviceRegistration
    } catch {
      case e: Exception =>
        LOG.severe(e.getMessage)
    }
  }

  override def deleted(pid: String) {
    for {
      s <- existingServices.remove(pid)
    } yield s.unregister()
  }

}