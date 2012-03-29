package edu.gemini.aspen.gds.obsevent.handler

import edu.gemini.aspen.giapi.data.ObservationEvent._
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}
import edu.gemini.aspen.gds.fits.FitsUpdater
import edu.gemini.aspen.gds.keywords.database.{Retrieve, Clean, KeywordsDatabase}
import edu.gemini.aspen.gds.actors.factory.CompositeActorsFactory
import edu.gemini.aspen.gds.actors._
import edu.gemini.aspen.gds.performancemonitoring._
import actors.Actor
import actors.Actor.actor
import java.io.{FileNotFoundException, File}
import java.util.logging.{Level, Logger}

import edu.gemini.aspen.gds.api.fits.Header
import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.gds.observationstate.ObservationStateRegistrar
import org.scala_tools.time.Imports._
import edu.gemini.aspen.gmp.services.PropertyHolder
import collection.mutable.ConcurrentMap
import com.google.common.cache.CacheBuilder
import java.util.concurrent.TimeUnit._

class ReplyHandler( actorsFactory: CompositeActorsFactory,
  keywordsDatabase: KeywordsDatabase,
  errorPolicy: ErrorPolicy,
  obsRegistry: ObservationStateRegistrar,
  propertyHolder: PropertyHolder) extends Actor {
  private val LOG = Logger.getLogger(this.getClass.getName)
  private val collectDeadline = 5000L
  private val eventLogger = new EventLogger
  private val bookKeep = new ObsEventBookKeeping

  import scala.collection.JavaConversions._

  // expiration of 1 day by default but tests can override it
  def expirationMillis = 24 * 60 * 60 * 1000

  val observationTransactions: ConcurrentMap[DataLabel, String] = CacheBuilder.newBuilder()
    .expireAfterWrite(expirationMillis, MILLISECONDS)
    .build[DataLabel, String]().asMap()

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
    LOG.info("ObservationEvent " + obsEvent + " for " + dataLabel)
    obsEvent match {
      case OBS_PREP => {
        eventLogger.addEventSet(dataLabel)
        obsRegistry.startObservation(dataLabel)
      }
      // This indicates that the observation was started by the seqexec using a "transaction" of sorts
      case EXT_START_OBS => {
        observationTransactions.put(dataLabel, "")
      }
      case _ =>
    }

    //check that all previous obsevents have arrived
    if (!bookKeep.previousArrived(obsEvent, dataLabel)) {
      LOG.warning("Received observation event " + obsEvent + " for datalabel " + dataLabel + " out of order")
    }
    eventLogger.start(dataLabel, obsEvent)
    bookKeep.addObs(obsEvent, dataLabel)

    new KeywordSetComposer(actorsFactory, keywordsDatabase) ! AcquisitionRequest(obsEvent, dataLabel)
  }

  def writeFinalFile(dataLabel: DataLabel, obsEvent: ObservationEvent) {
    {
      //if all obsevents replies have arrived
      if (bookKeep.allRepliesArrived(dataLabel)) {
        bookKeep.clean(dataLabel)
        endWrite(dataLabel)
        endAcqRequestReply(obsEvent, dataLabel)
      } else {
        LOG.warning("Received data collection reply for " + obsEvent + " for dataset " + dataLabel + ", but data collection on other observation events hasn't finished. Wait and retry.")
        //sleep one second and retry (5 times)
        actor {
          def retry(retries: Int, sleep: Long) {
            Thread.sleep(sleep)
            if (bookKeep.allRepliesArrived(dataLabel)) {
              //OK, can continue
              bookKeep.clean(dataLabel)
              endWrite(dataLabel)
              endAcqRequestReply(obsEvent, dataLabel)
            } else if (retries > 1) {
              //retry
              LOG.warning("Still haven't completed data collection on other observation events. Wait and retry.")
              retry(retries - 1, sleep)
            } else {
              //we failed, wrap things up anyway
              bookKeep.clean(dataLabel)
              endWrite(dataLabel)
              endAcqRequestReply(obsEvent, dataLabel)
              LOG.severe("Retry limit for " + obsEvent + " for dataset " + dataLabel + " reached. FITS file is probably incomplete.")
            }
          }
          retry(5, 1000)
        }
      }
    }
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
      case OBS_END_DSET_WRITE if !observationTransactions.contains(dataLabel) => writeFinalFile(dataLabel, obsEvent)
      case EXT_END_OBS if observationTransactions.contains(dataLabel) => writeFinalFile(dataLabel, obsEvent)
      case _ => endAcqRequestReply(obsEvent, dataLabel)
    }

  }

  private def endAcqRequestReply(obsEvent: ObservationEvent, dataLabel: DataLabel) {
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
    //if the option is None, use an empty List
    val list = (keywordsDatabase !? Retrieve(dataLabel)).asInstanceOf[List[CollectedValue[_]]]
    val processedList = errorPolicy.applyPolicy(dataLabel, list)
    val maxHeader = (0 /: processedList)((i, m) => m.index.max(i))

    val headers: List[Header] = List.range(0, maxHeader + 1) map {
      headerIndex => {
        val headerItems = processedList filter {
          _.index == headerIndex
        } map {
          _ match {
            // Implicit conversion
            case c => c._type.collectedValueToHeaderItem(c)
          }
        }
        new Header(headerIndex, headerItems)
      }

    }

    actor {
      eventLogger.start(dataLabel, "FITS update")
      val start = new DateTime
      (try {
        Some(new FitsUpdater(new File(propertyHolder.getProperty("DHS_SCIENCE_DATA_PATH")), new File(propertyHolder.getProperty("DHS_PERMANENT_SCIENCE_DATA_PATH")), dataLabel, headers))
      } catch {
        case ex => {
          obsRegistry.registerError(dataLabel, "Problem writing FITS file")
          LOG.log(Level.SEVERE, ex.getMessage, ex)
          None
        }
      }) map {
        updater: FitsUpdater => {
          try {
            updater.updateFitsHeaders()
          } catch {
            case ex => {
              obsRegistry.registerError(dataLabel, "Problem writing FITS file")
              LOG.log(Level.SEVERE, ex.getMessage, ex)
            }
          }
        }
      }
      val end = new DateTime
      LOG.info("Writing updated FITS file at " + new File(dataLabel.toString) + " took " + (start to end).toDuration)
      eventLogger.end(dataLabel, "FITS update")
      obsRegistry.registerTimes(dataLabel, eventLogger.retrieve(dataLabel).toTraversable)
      obsRegistry.endObservation(dataLabel)
    }
  }

}