package edu.gemini.aspen.gds.health

import org.apache.felix.ipojo.annotations._
import java.util.logging.Logger
import edu.gemini.jms.api.JmsProvider
import edu.gemini.aspen.giapi.status.impl.HealthStatus
import edu.gemini.aspen.giapi.status.Health
import edu.gemini.aspen.giapi.util.jms.status.StatusSetter
import edu.gemini.aspen.gds.api.{KeywordSource, KeywordActorsFactory}
import edu.gemini.aspen.giapi.data.ObservationEventHandler
import edu.gemini.aspen.gds.obsevent.handler.GDSObseventHandler

/**
 * OSGi component providing health information for the GDS
 */
@Component
@Instantiate
class GdsHealth(@Requires provider: JmsProvider) {

  private val healthName = "gpi:gds:health"
  private val healthSetter: StatusSetter = new StatusSetter("GDS Health", healthName)
  private val LOG = Logger.getLogger(this.getClass.getName)

  private val healthState = new HealthState
  private var validated = false

  private def updateHealth() {
    LOG.info("Updating Health to " + healthState.getHealth)
    if (validated) {
      healthSetter.setStatusItem(new HealthStatus(healthName, healthState.getHealth))
    }
  }

  @Validate
  def validate() {
    LOG.info("Validating GDS Health")
    validated = true
    healthSetter.startJms(provider)
    updateHealth()
  }

  @Invalidate
  def invalidate() {
    LOG.info("Invalidating GDS Health")
    validated = false
    healthSetter.stopJms()
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
    if (evtHndlr.isInstanceOf[GDSObseventHandler]) {
      healthState.registerGDSObseventHandler()
      updateHealth()
    }
  }

  @Unbind(aggregate = true, specification = "edu.gemini.aspen.giapi.data.ObservationEventHandler", optional = true)
  def unbindGDSObseventHandler(evtHndlr: ObservationEventHandler) {
    LOG.info("Unbinding GDSObseventHandler")
    if (evtHndlr.isInstanceOf[GDSObseventHandler]) {
      healthState.unregisterGDSObseventHandler()
      updateHealth()
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


  private class HealthState {
    private val LOG = Logger.getLogger(this.getClass.getName)

    private val actors = new Array[Boolean](KeywordSource.maxId - 1) //Booleans are initialized to false

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