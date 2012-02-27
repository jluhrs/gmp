package edu.gemini.aspen.gds.health

import org.apache.felix.ipojo.annotations._
import java.util.logging.Logger
import edu.gemini.aspen.giapi.status.impl.HealthStatus
import edu.gemini.aspen.giapi.status.Health
import edu.gemini.aspen.giapi.util.jms.status.StatusSetter
import edu.gemini.aspen.gds.api.{KeywordSource, KeywordActorsFactory}
import edu.gemini.aspen.giapi.data.ObservationEventHandler
import edu.gemini.aspen.gds.obsevent.handler.GDSObseventHandler

import scala.actors.Actor._
import actors.Actor
import edu.gemini.jms.api.{JmsArtifact, JmsProvider}
import edu.gemini.aspen.gmp.top.Top

case object UpdateHealth

case class StartJms(provider: JmsProvider)

case object StopJms

/**
 * OSGi component providing health information for the GDS
 */
@Component
@Instantiate
@Provides(specifications = Array[Class[_]](classOf[JmsArtifact]))
class GdsHealth(@Requires top: Top) extends JmsArtifact {

  private var healthName: String = _
  private val healthSetter: StatusSetter = new StatusSetter("GDS Health", healthName)
  private val LOG = Logger.getLogger(this.getClass.getName)

  private val healthState = new HealthState
  private val stateActor = new StateActor()
  stateActor.start()

  @Validate
  def validate() {
    healthName = top.buildStatusItemName("gds:health")
  }

  private def updateHealth() {
    LOG.info("Updating Health to " + healthState.getHealth + " " + healthSetter)
    stateActor ! UpdateHealth
  }

  override def startJms(provider: JmsProvider) {
    LOG.info("Validating GDS Health")
    stateActor ! StartJms(provider)
  }

  override def stopJms() {
    LOG.info("Invalidating GDS Health")
    stateActor ! StopJms
  }

  @Bind(specification = "edu.gemini.aspen.gds.staticheaderreceiver.HeaderReceiver", optional = true)
  def bindHeaderReceiver() {
    LOG.info("Binding HeaderReceiver")
    healthState.registerHeaderReceiver()
    updateHealth()
  }

  @Unbind(specification = "edu.gemini.aspen.gds.staticheaderreceiver.HeaderReceiver", optional = true)
  def unbindHeaderReceiver() {
    LOG.info("Unbinding HeaderReceiver")
    healthState.unregisterHeaderReceiver()
    updateHealth()
  }

  @Bind(aggregate = true, specification = "edu.gemini.aspen.giapi.data.ObservationEventHandler", optional = true)
  def bindGDSObseventHandler(evtHndlr: ObservationEventHandler) {
    LOG.info("Binding GDSObseventHandler")
    evtHndlr match {
      case e: GDSObseventHandler =>
        healthState.registerGDSObseventHandler()
        updateHealth()
      case _ => LOG.info("Ignoring observation event handler: " + evtHndlr)
    }
  }

  @Unbind(aggregate = true, specification = "edu.gemini.aspen.giapi.data.ObservationEventHandler", optional = true)
  def unbindGDSObseventHandler(evtHndlr: ObservationEventHandler) {
    LOG.info("Unbinding GDSObseventHandler")
    evtHndlr match {
      case e: GDSObseventHandler =>
        healthState.unregisterGDSObseventHandler()
        updateHealth()
      case _ => LOG.info("Ignoring observation event handler: " + evtHndlr)
    }
  }

  @Bind(aggregate = true, optional = true)
  def bindActorFactory(fact: KeywordActorsFactory) {
    LOG.info("Binding ActorsFactory: " + fact.getClass.getName)
    healthState.registerActorFactory(fact.getSource)
    updateHealth()
  }

  @Unbind(aggregate = true, optional = true)
  def unbindActorFactory(fact: KeywordActorsFactory) {
    LOG.info("Unbinding ActorsFactory: " + fact.getClass.getName)
    healthState.unregisterActorFactory(fact.getSource)
    updateHealth()
  }

  class StateActor extends Actor {
    private var validated = false

    override def act() {
      loop {
        react {
          case UpdateHealth =>
            if (validated) {
              healthSetter.setStatusItem(new HealthStatus(healthName, healthState.getHealth))
            }
          case StartJms(provider) =>
            healthSetter.startJms(provider)
            validated = true
            self ! UpdateHealth
          case StopJms =>
            validated = false
            healthSetter.stopJms()
        }
      }
    }
  }

  private class HealthState {
    private val LOG = Logger.getLogger(this.getClass.getName)

    private val actors = new Array[Boolean](5) //Booleans are initialized to false

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
        case KeywordSource.NONE => LOG.info("Registered KeywordActorsFactory of unknown type: " + source)
        case x => actors(x.id) = true
      }
    }

    def unregisterActorFactory(source: KeywordSource.Value) {
      source match {
        case KeywordSource.NONE => LOG.info("Unregistered KeywordActorsFactory of unknown type: " + source)
        case x => actors(x.id) = false
      }
    }

    def getHealth = {
      LOG.info("HeaderReceiver: " + headerRec + ", ObservationEventHandler: " + obsEvtHndl + ", Actor factories: " + actors.toList)
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
  }

}