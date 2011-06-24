package edu.gemini.aspen.gds.obsevent.handler

import edu.gemini.aspen.giapi.data.ObservationEvent._
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel, ObservationEventHandler}
import org.apache.felix.ipojo.annotations.{Requires, Provides, Instantiate, Component}
import edu.gemini.aspen.gds.fits.FitsUpdater
import edu.gemini.aspen.gds.keywords.database.{Retrieve, Clean, KeywordsDatabase}
import edu.gemini.aspen.gds.actors.factory.CompositeActorsFactory
import edu.gemini.aspen.gds.actors._
import edu.gemini.aspen.gds.performancemonitoring._
import actors.Actor
import actors.Actor.actor
import java.io.{FileNotFoundException, File}
import java.util.logging.{Level, Logger}
import edu.gemini.aspen.gds.api.Conversions._
import edu.gemini.aspen.gds.api.CollectedValue._
import edu.gemini.fits.{DefaultHeaderItem, DefaultHeader, Header}
import java.lang.Double
import edu.gemini.aspen.gds.api._
import sun.net.www.HeaderParser

/**
 * Simple Observation Event Handler that creates a KeywordSetComposer and launches the
 * keyword values acquisition process
 */
@Component
@Instantiate
@Provides(specifications = Array(classOf[ObservationEventHandler]))
class GDSObseventHandler(@Requires actorsFactory: CompositeActorsFactory, @Requires keywordsDatabase: KeywordsDatabase, @Requires errorPolicy: CompositeErrorPolicy) extends ObservationEventHandler {
    private val replyHandler = new ReplyHandler(actorsFactory, keywordsDatabase, errorPolicy)

    def onObservationEvent(event: ObservationEvent, dataLabel: DataLabel) {
        replyHandler ! AcquisitionRequest(event, dataLabel)
    }

}

class ReplyHandler(actorsFactory: CompositeActorsFactory, keywordsDatabase: KeywordsDatabase, errorPolicy: ErrorPolicy) extends Actor {
    private val LOG = Logger.getLogger(this.getClass.getName)
    private val collectDeadline = 300L
    private val eventLogger = new EventLogger
    private val bookKeep = new ObsEventBookKeeping

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

        obsEvent match {
            case OBS_PREP => {
                eventLogger.addEventSet(dataLabel)
            }
            case _ =>
        }

        //check that all previous obsevents have arrived
        if (!bookKeep.previousArrived(obsEvent, dataLabel)) {
            LOG.severe("Received observation event " + obsEvent + " for datalabel " + dataLabel + " out of order")
        }
        eventLogger.start(dataLabel, obsEvent)
        bookKeep.addObs(obsEvent, dataLabel)

        new KeywordSetComposer(actorsFactory, keywordsDatabase) ! AcquisitionRequest(obsEvent, dataLabel)

    }

    private def acqRequestReply(obsEvent: ObservationEvent, dataLabel: DataLabel) {


        //check that this obsevent collection reply hasn't already arrived but that the obsevent has.
        if (bookKeep.replyArrived(obsEvent, dataLabel)) {
            LOG.severe("Received data collection reply for observation event " + obsEvent + " for datalabel " + dataLabel + " twice")
            return
        }
        if (!bookKeep.obsArrived(obsEvent, dataLabel)) {
            LOG.severe("Received data collection reply for observation event " + obsEvent + " for datalabel " + dataLabel + ", but never received the observation event")
            return
        }
        bookKeep.addReply(obsEvent, dataLabel)

        obsEvent match {
            case OBS_END_DSET_WRITE => {
                //if all obsevents replies have arrived
                if (bookKeep.allRepliesArrived(dataLabel)) {
                    bookKeep.clean(dataLabel)
                    endWrite(dataLabel)
                } else {
                    LOG.severe("Received data collection reply for " + obsEvent + " for dataset " + dataLabel + ", but data collection on other observation events hasn't finished")
                    //todo: sleep one second and retry
                    //                    actor {
                    //                        Thread.sleep(1000)
                    //                        this ! AcquisitionRequestReply(obsEvent, dataLabel)
                    //                    }
                    return
                }
            }
            case _ =>
        }

        eventLogger.end(dataLabel, obsEvent)

        enforceTimeConstraints(obsEvent, dataLabel)

        obsEvent match {
            case OBS_END_DSET_WRITE => {
                //log timing stats for this datalabel
                for (evt <- ObservationEvent.values()) {
                    logTiming(evt, dataLabel)
                }
            }
            case _ =>
        }
    }

    private def enforceTimeConstraints(evt: ObservationEvent, label: DataLabel) {
        evt match {
            case OBS_START_ACQ => {
                checkTime(evt, label)
            }
            case OBS_END_ACQ => {
                checkTime(evt, label)
            }
            case _ =>
        }
    }

    private def logTiming(evt: ObservationEvent, label: DataLabel) {
        val avgTime = eventLogger.average(evt) map {
            x => x.getMillis
        } getOrElse {
            "unknown"
        }
        val currTime = eventLogger.retrieve(label, evt) map {
            x => x.getMillis
        } getOrElse {
            "unknown"
        }
        LOG.info("Average timing for event " + evt + ": " + avgTime + "[ms]")
        LOG.info("Timing for event " + evt + " DataLabel " + label + ": " + currTime + "[ms]")
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

    private def updateFITSFile(dataLabel: DataLabel) {
        val list = (keywordsDatabase !? Retrieve(dataLabel)).asInstanceOf[Option[List[CollectedValue[_]]]]
        // todo consider the case this is none
        val processedList = errorPolicy.applyPolicy(dataLabel, list).get
        val maxHeader = (0 /: processedList)((i, m) => m.index.max(i))

        val headers: List[Header] = List.range(0, maxHeader + 1) map {
            headerIndex => {
                val header = new DefaultHeader(headerIndex)
                processedList filter {
                    _.index == headerIndex
                } foreach {
                    _ match {
                        // Implicit conversion
                        case c => header.add(c._type.collectedValueToHeaderItem(c))
                    }
                }
                header
            }
        }

        processedList map {
            headersList => actor {
                new FitsUpdater(new File("/tmp"), dataLabel, headers).updateFitsHeaders()
            }
        }
    }

}