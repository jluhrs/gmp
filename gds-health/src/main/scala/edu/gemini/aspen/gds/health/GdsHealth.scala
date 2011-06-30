package edu.gemini.aspen.gds.health

import org.apache.felix.ipojo.annotations._
import java.util.logging.Logger
import edu.gemini.jms.api.JmsProvider
import edu.gemini.aspen.giapi.status.impl.HealthStatus
import edu.gemini.aspen.giapi.status.Health
import edu.gemini.aspen.giapi.util.jms.status.StatusSetter
import edu.gemini.aspen.gds.api.{KeywordSource, KeywordActorsFactory}

/**
 * OSGi component providing health information for the GDS
 */
@Component
@Instantiate
class GdsHealth(@Requires provider: JmsProvider) {

    private val healthName = "gpi:gds:health"
    private val healthSetter: StatusSetter = new StatusSetter(healthName)
    private val LOG = Logger.getLogger(this.getClass.getName)

    private val actors = new Array[Boolean](KeywordSource.maxId - 1)

    private var obsEvtHndl = false
    private var headerRec = false
    private var validated = false

    private def updateHealth() {
        LOG.info("Updating Health")
        LOG.fine("HeaderReceiver: " + headerRec + ", ObservationEventHandler: " + obsEvtHndl + ", Actor factories: " + actors.toList)
        if (validated) {
            if (obsEvtHndl) {
                if (actors.reduceLeft({
                    _ && _
                }) && headerRec) {
                    healthSetter.setStatusItem(new HealthStatus(healthName, Health.GOOD))
                } else {
                    healthSetter.setStatusItem(new HealthStatus(healthName, Health.WARNING))
                }
            } else {
                healthSetter.setStatusItem(new HealthStatus(healthName, Health.BAD))
            }
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
        headerRec = true
        updateHealth()
    }

    @Unbind(specification = "edu.gemini.aspen.gds.staticheaderreceiver.HeaderReceiver", optional = true)
    def unbindHeaderReceiver() {
        LOG.info("Unbinding HeaderReceiver")
        headerRec = false
        updateHealth()
    }

    @Bind(id = "GDSObseventHandler", specification = "edu.gemini.aspen.giapi.data.ObservationEventHandler")
    def bindGDSObseventHandler() {
        LOG.info("Binding GDSObseventHandler")
        obsEvtHndl = true
        updateHealth()
    }

    @Unbind(id = "GDSObseventHandler", specification = "edu.gemini.aspen.giapi.data.ObservationEventHandler")
    def unbindGDSObseventHandler() {
        LOG.info("Unbinding GDSObseventHandler")
        obsEvtHndl = false
        updateHealth()
    }

    @Bind(aggregate = true, optional = true)
    def bindActorFactory(fact: KeywordActorsFactory) {
        LOG.info("Binding ActorsFactory: " + fact.getClass.getName)
        fact.getSource match {
            case KeywordSource.NONE => LOG.info("Registered KeywordActorsFactory of unknown type: " + fact.getSource)
            case x => actors(x.id) = true
        }
        updateHealth()
    }

    @Unbind(aggregate = true, optional = true)
    def unbindActorFactory(fact: KeywordActorsFactory) {
        LOG.info("Unbinding ActorsFactory: " + fact.getClass.getName)
        fact.getSource match {
            case KeywordSource.NONE => LOG.info("Unregistered KeywordActorsFactory of unknown type: " + fact.getSource)
            case x => actors(x.id) = false
        }
        updateHealth()
    }
}