package edu.gemini.aspen.gds.health

import org.apache.felix.ipojo.annotations._
import java.util.logging.Logger
import edu.gemini.aspen.giapi.status.impl.{BasicStatus, HealthStatus}
import edu.gemini.aspen.giapi.status.Health
import edu.gemini.aspen.gds.api.{KeywordSource, KeywordActorsFactory}

import actors.Actor
import edu.gemini.aspen.gmp.top.Top
import edu.gemini.aspen.gds.obsevent.handler.GDSObseventHandler
import collection.mutable.ListBuffer
import edu.gemini.jms.api.{JmsProvider, JmsArtifact}
import scala.actors.threadpool.TimeUnit
import edu.gemini.aspen.giapi.status.setter.StatusSetter

case object UpdateHealth
case object Connected

/**
 * OSGi component providing health information for the GDS
 */
@Component
@Instantiate
@Provides
class GdsHealth(@Requires top: Top, @Requires setter: StatusSetter) extends JmsArtifact {
  implicit private val LOG = Logger.getLogger(this.getClass.getName)

  private val healthName = top.buildStatusItemName("gds:health")
  private val healthMessageName = top.buildStatusItemName("gds:health:message")

  private val healthState = new HealthState
  private val stateActor = new StateActor()
  stateActor.start()

  private def updateHealth() {
    LOG.info(s"Updating Health to ${healthState.getHealth} on $healthName")
    stateActor ! UpdateHealth
  }

  def stopJms() {}

  def startJms(provider: JmsProvider) {
    LOG.info("Start GDS Health")
    stateActor ! Connected
  }

  @Bind(specification = "edu.gemini.aspen.gds.staticheaderreceiver.HeaderReceiver", optional = true)
  def bindHeaderReceiver() {
    LOG.fine("Binding HeaderReceiver")
    healthState.registerHeaderReceiver()
    updateHealth()
  }

  @Unbind(specification = "edu.gemini.aspen.gds.staticheaderreceiver.HeaderReceiver", optional = true)
  def unbindHeaderReceiver() {
    LOG.fine("Unbinding HeaderReceiver")
    healthState.unregisterHeaderReceiver()
    updateHealth()
  }

  @Bind(aggregate = true, specification = "edu.gemini.aspen.gds.obsevent.handler.GDSObseventHandler", optional = true)
  def bindGDSObseventHandler(evtHndlr: GDSObseventHandler) {
    LOG.fine("Binding GDSObseventHandlerImpl")
    healthState.registerGDSObseventHandler()
    updateHealth()
  }

  @Unbind(aggregate = true, specification = "edu.gemini.aspen.gds.obsevent.handler.GDSObseventHandler", optional = true)
  def unbindGDSObseventHandler(evtHndlr: GDSObseventHandler) {
    LOG.fine("Unbinding GDSObseventHandlerImpl")
    healthState.unregisterGDSObseventHandler()
    updateHealth()
  }

  @Bind(aggregate = true, optional = true)
  def bindActorFactory(fact: KeywordActorsFactory) {
    LOG.fine("Binding ActorsFactory: " + fact.getClass.getName)
    healthState.registerActorFactory(fact.getSource)
    updateHealth()
  }

  @Unbind(aggregate = true, optional = true)
  def unbindActorFactory(fact: KeywordActorsFactory) {
    LOG.fine("Unbinding ActorsFactory: " + fact.getClass.getName)
    healthState.unregisterActorFactory(fact.getSource)
    updateHealth()
  }

  class StateActor extends Actor {
    var connected = false

    override def act() {
      loop {
        react {
          case Connected                 =>
              TimeUnit.SECONDS.sleep(1)
              LOG.info("GDS Health Connected")
              connected = true
              updateHealthValues()
          case UpdateHealth if connected =>
            updateHealthValues()
          case UpdateHealth              =>
        }
      }
    }

    def updateHealthValues() {
      setter.setStatusItem(new HealthStatus(healthName, healthState.getHealth))
      setter.setStatusItem(new BasicStatus[String](healthMessageName, healthState.getMessage))
      LOG.info("GDS health SET " + healthState)
    }
  }

  private class HealthState(implicit val LOG: Logger) {

    private val actors = ListBuffer[Boolean](false, false, true /* ODB is disabled*/, false, false, false) //Booleans are initialized to false

    private var obsEvtHndl = false
    private var headerRec = false

    def registerHeaderReceiver() {
      headerRec = true
    }

    def unregisterHeaderReceiver() {
      headerRec = false
    }

    def registerGDSObseventHandler() {
      obsEvtHndl = true
    }

    def unregisterGDSObseventHandler() {
      obsEvtHndl = false
    }

    def registerActorFactory(source: KeywordSource.Value) {
      source match {
        case KeywordSource.NONE => LOG.fine(s"Registered KeywordActorsFactory of unknown type: $source")
        case x => actors(x.id) = true
      }
    }

    def unregisterActorFactory(source: KeywordSource.Value) {
      source match {
        case KeywordSource.NONE => LOG.fine(s"Unregistered KeywordActorsFactory of unknown type: $source")
        case x => actors(x.id) = false
      }
    }

    def getHealth = {
      if (obsEvtHndl) {
        if (actors.reduceLeft({
          _ && _
        }) && headerRec) {
          Health.GOOD
        } else {
          Health.WARNING
        }
      } else {
        Health.BAD
      }
    }

    def getMessage = {
      getHealth match {
        case Health.GOOD => ""
        case _ => "Missing components: " +
          (if (!headerRec) "HeaderReceiver, " else "") +
          (if (!obsEvtHndl) "ObservationEventHandler, " else "") +
          (actors.indices collect {
            case x if actors(x) == false => KeywordSource(x).toString
          } reduceLeftOption ({
            _ + ", " + _
          }) map {
            case x => "Actor factories: " + x
          } getOrElse "")
      }
    }
  }

}