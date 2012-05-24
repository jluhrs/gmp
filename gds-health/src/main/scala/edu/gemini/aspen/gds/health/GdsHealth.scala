package edu.gemini.aspen.gds.health

import org.apache.felix.ipojo.annotations._
import java.util.logging.Logger
import edu.gemini.aspen.giapi.status.impl.HealthStatus
import edu.gemini.aspen.giapi.status.Health
import edu.gemini.aspen.giapi.util.jms.status.StatusSetter
import edu.gemini.aspen.gds.api.{KeywordSource, KeywordActorsFactory}

import scala.actors.Actor._
import actors.Actor
import edu.gemini.jms.api.{JmsArtifact, JmsProvider}
import edu.gemini.aspen.gmp.top.Top
import edu.gemini.aspen.gds.obsevent.handler.GDSObseventHandler

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
  implicit private val LOG = Logger.getLogger(this.getClass.getName)

  private val healthName = top.buildStatusItemName("gds:health")
  private val healthSetter = new StatusSetter("GDS Health", healthName)

  private val healthState = new HealthState
  private val stateActor = new StateActor()
  stateActor.start()

  @Validate
  def validate() {
    // Required by iPojo
  }

  private def updateHealth() {
    LOG.info("Updating Health to " + healthState.getHealth + " on " + healthName)
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

  private class HealthState(implicit val LOG:Logger) {

    private val actors = new Array[Boolean](6) //Booleans are initialized to false

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
        case KeywordSource.NONE => LOG.fine("Registered KeywordActorsFactory of unknown type: " + source)
        case x => actors(x.id) = true
      }
    }

    def unregisterActorFactory(source: KeywordSource.Value) {
      source match {
        case KeywordSource.NONE => LOG.fine("Unregistered KeywordActorsFactory of unknown type: " + source)
        case x => actors(x.id) = false
      }
    }

    def getHealth = {
      val actorsStr = actors.zipWithIndex.map {
        case (a, i) => "%s -> %b".format(KeywordSource(i), a)
      }
      LOG.fine("HeaderReceiver: " + headerRec + ", ObservationEventHandler: " + obsEvtHndl + ", Actor factories: " + actorsStr.mkString(", "))
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