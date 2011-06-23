package edu.gemini.aspen.gds.obsevent.handler

import edu.gemini.aspen.giapi.data.ObservationEvent._
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel, ObservationEventHandler}
import org.apache.felix.ipojo.annotations.{Requires, Provides, Instantiate, Component}
import edu.gemini.aspen.gds.fits.FitsUpdater
import edu.gemini.aspen.gds.keywords.database.{Retrieve, Clean, KeywordsDatabase}
import edu.gemini.fits.Header
import edu.gemini.aspen.gds.actors.factory.CompositeActorsFactory
import edu.gemini.aspen.gds.actors._
import edu.gemini.aspen.gds.performancemonitoring._
import actors.Actor
import actors.Actor.actor
import java.io.{FileNotFoundException, File}
import java.util.logging.{Level, Logger}
import java.util.EnumSet
import edu.gemini.aspen.gds.api.{CompositeErrorPolicy, ErrorPolicy}

/**
 * Simple Observation Event Handler that creates a KeywordSetComposer and launches the
 * keyword values acquisition process
 */
@Component
@Instantiate
@Provides(specifications = Array(classOf[ObservationEventHandler]))
class GDSObseventHandler(@Requires actorsFactory: CompositeActorsFactory, @Requires keywordsDatabase: KeywordsDatabase, @Requires errorPolicy: CompositeErrorPolicy) extends ObservationEventHandler {
    private val LOG = Logger.getLogger(this.getClass.getName)
    //todo: private[handler] is just for testing. Need to find a better way to test this class
    private[handler] val replyHandler = new ReplyHandler(actorsFactory, keywordsDatabase, errorPolicy)

    def onObservationEvent(event: ObservationEvent, dataLabel: DataLabel) {
        replyHandler ! AcquisitionRequest(event, dataLabel)
    }

}

class ReplyHandler(actorsFactory: CompositeActorsFactory, keywordsDatabase: KeywordsDatabase, errorPolicy: ErrorPolicy) extends Actor {
    private val LOG = Logger.getLogger(this.getClass.getName)
    private val collectDeadline = 300L
    private val eventLogger = new EventLogger
    private val obsEventsMap = collection.mutable.Map.empty[DataLabel, EnumSet[ObservationEvent]]
    private val repliesMap = collection.mutable.Map.empty[DataLabel, EnumSet[ObservationEvent]]

    start()

    def act() {
        loop {
            react {
                case AcquisitionRequest(obsEvent, dataLabel) => acqRequest(obsEvent, dataLabel)
                case AcquisitionRequestReply(obsEvent, dataLabel) => acqRequestReply(obsEvent, dataLabel)
                case x: Any => throw new RuntimeException("Argument not known " + x)
            }
        }
    }

    private def acqRequest(obsEvent: ObservationEvent, dataLabel: DataLabel) {
        val obsSet = obsEventsMap.getOrElseUpdate(dataLabel, EnumSet.noneOf(classOf[ObservationEvent]))

        obsEvent match {
            case OBS_PREP => {
                eventLogger.addEventSet(dataLabel)
            }
            case _ =>
        }

        //check that all previous obsevents have arrived
        if (!EnumSet.complementOf(EnumSet.range(obsEvent, ObservationEvent.values().last)).equals(obsSet)) {
            LOG.severe("Received observation event " + obsEvent + " for datalabel " + dataLabel + " out of order")
        }
        obsSet.add(obsEvent)
        eventLogger.start(dataLabel, obsEvent)

        new KeywordSetComposer(actorsFactory, keywordsDatabase) ! AcquisitionRequest(obsEvent, dataLabel)

    }

    private def acqRequestReply(obsEvent: ObservationEvent, dataLabel: DataLabel) {

        val repliesSet = repliesMap.getOrElseUpdate(dataLabel, EnumSet.noneOf(classOf[ObservationEvent]))

        //check that this obsevent collection reply hasn't already arrived but that the obsevent has.
        if (repliesSet.contains(obsEvent)) {
            LOG.severe("Received data collection reply for observation event " + obsEvent + " for datalabel " + dataLabel + " twice")
            return
        }
        if (!obsEventsMap(dataLabel).contains(obsEvent)) {
            LOG.severe("Received data collection reply for observation event " + obsEvent + " for datalabel " + dataLabel + ", but never received the observation event")
            return
        }
        repliesSet.add(obsEvent)

        obsEvent match {
            case OBS_END_DSET_WRITE => {
                //if all obsevents replies have arrived
                if (repliesMap.getOrElse(dataLabel, EnumSet.noneOf(classOf[ObservationEvent])).containsAll(EnumSet.allOf(classOf[ObservationEvent]))) {
                    repliesMap -= dataLabel
                    obsEventsMap -= dataLabel
                    endWrite(dataLabel)
                } else {
                    LOG.severe("Received data collection reply for " + obsEvent + " for dataset " + dataLabel + ", but data collection on other observation events hasn't finished")
                    //sleep one second and retry
                    actor {
                        Thread.sleep(1000)
                        this ! AcquisitionRequestReply(obsEvent, dataLabel)
                    }
                    return
                }
            }
            case _ =>
        }

        eventLogger.end(dataLabel, obsEvent)

        //enforce timing constraints
        obsEvent match {
            case OBS_START_ACQ => {
                checkTime(obsEvent, dataLabel)
            }
            case OBS_END_ACQ => {
                checkTime(obsEvent, dataLabel)
            }
            case _ =>
        }

        obsEvent match {
            case OBS_END_DSET_WRITE => {
                //log timing stats for this datalabel
                LOG.info(eventLogger.retrieve(dataLabel).toString())

                LOG.info("Average timing for event " + OBS_PREP + ": " + eventLogger.average(OBS_PREP).flatMap({
                    x => Some(x.getMillis)
                }).getOrElse("unknown") + "[ms]")
                LOG.info("Average timing for event " + OBS_START_ACQ + ": " + eventLogger.average(OBS_START_ACQ).flatMap({
                    x => Some(x.getMillis)
                }).getOrElse("unknown") + "[ms]")
                LOG.info("Average timing for event " + OBS_END_ACQ + ": " + eventLogger.average(OBS_END_ACQ).flatMap({
                    x => Some(x.getMillis)
                }).getOrElse("unknown") + "[ms]")
                LOG.info("Average timing for event " + OBS_END_DSET_WRITE + ": " + eventLogger.average(OBS_END_DSET_WRITE).flatMap({
                    x => Some(x.getMillis)
                }).getOrElse("unknown") + "[ms]")
            }
            case _ =>
        }
    }

    private def checkTime(obsEvent: ObservationEvent, dataLabel: DataLabel) {
        //check if the keyword recollection was performed on time
        eventLogger.check(dataLabel, obsEvent, collectDeadline) match {
            case onTime: Boolean => if (!onTime) {
                LOG.severe("Dataset " + dataLabel + ", Event " + obsEvent + ", didn't finish on time")
            }
            case _ => LOG.severe("Performance monitoring module failed to respond")
        }
    }

    private def endWrite(dataLabel: DataLabel) {
        try {
            updateFITSFile(dataLabel)
        } catch {
            case ex: FileNotFoundException => LOG.log(Level.SEVERE, ex.getMessage, ex)

        }
        keywordsDatabase ! Clean(dataLabel)
    }

    private def updateFITSFile(dataLabel: DataLabel): Unit = {
        val list = (keywordsDatabase !? Retrieve(dataLabel)).asInstanceOf[Option[List[Header]]]
        val processedList = errorPolicy.applyPolicy(dataLabel, list)

        processedList map {
            headersList => actor {
                new FitsUpdater(new File("/tmp"), dataLabel, headersList).updateFitsHeaders()
            }
        }
    }

}