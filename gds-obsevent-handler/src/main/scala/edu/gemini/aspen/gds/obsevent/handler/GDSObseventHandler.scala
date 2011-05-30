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
import java.io.{FileNotFoundException, File}
import java.util.logging.{Level, Logger}

/**
 * Simple Observation Event Handler that creates a KeywordSetComposer and launches the
 * keyword values acquisition process
 */
@Component
@Instantiate
@Provides(specifications = Array(classOf[ObservationEventHandler]))
class GDSObseventHandler(@Requires actorsFactory: CompositeActorsFactory, @Requires keywordsDatabase: KeywordsDatabase) extends ObservationEventHandler {
  private val LOG = Logger.getLogger(this.getClass.getName)
  //todo: private[handler] is just for testing. Need to find a better way to test this class
  private[handler] val replyHandler = new ReplyHandler(actorsFactory, keywordsDatabase)

  def onObservationEvent(event: ObservationEvent, dataLabel: DataLabel) {
    event match {
      case OBS_PREP => replyHandler ! PrepareObservation(dataLabel)
      case OBS_START_ACQ => replyHandler ! StartAcquisition(dataLabel)
      case OBS_END_ACQ => replyHandler ! EndAcquisition(dataLabel)
      case OBS_START_READOUT =>
      case OBS_END_READOUT =>
      case OBS_START_DSET_WRITE =>
      case OBS_END_DSET_WRITE => replyHandler ! EndWrite(dataLabel)
      case e: ObservationEvent => LOG.info("Non handled observation event: " + e)
    }
  }

}

class ReplyHandler(actorsFactory: CompositeActorsFactory, keywordsDatabase: KeywordsDatabase) extends Actor {
  private val LOG = Logger.getLogger(this.getClass.getName)
  private val collectDeadline = 300L
  private val eventLogger = new EventLogger
  start()

  def act() {
    loop {
      react {
        case PrepareObservation(dataLabel) => prepareObservation(dataLabel)
        case PrepareObservationReply(dataLabel) => prepareObservationReply(dataLabel)
        case StartAcquisition(dataLabel) => startAcquisition(dataLabel)
        case StartAcquisitionReply(dataLabel) => startAcquisitionReply(dataLabel)
        case EndAcquisition(dataLabel) => endAcquisition(dataLabel)
        case EndAcquisitionReply(dataLabel) => endAcquisitionReply(dataLabel)
        case EndWrite(dataLabel) => endWrite(dataLabel)
        case x: Any => throw new RuntimeException("Argument not known " + x)
      }
    }
  }

  //set of observations that have completed the OBS_PREP header collection
  private var prepared: Set[DataLabel] = Set[DataLabel]()

  //set of observations that have completed the OBS_START_ACQ header collection
  private var started: Set[DataLabel] = Set[DataLabel]()

  //set of observations that have completed the OBS_END_ACQ header collection
  private var ended: Set[DataLabel] = Set[DataLabel]()

  private def prepareObservation(dataLabel: DataLabel) {
    eventLogger.addEventSet(dataLabel)
    eventLogger.start(dataLabel, OBS_PREP)
    new KeywordSetComposer(actorsFactory, keywordsDatabase) ! PrepareObservation(dataLabel)
  }

  private def prepareObservationReply(dataLabel: DataLabel) {
    prepared += dataLabel
    eventLogger.end(dataLabel, OBS_PREP)
  }

  private def startAcquisition(dataLabel: DataLabel) {
    eventLogger.start(dataLabel, OBS_START_ACQ)
    new KeywordSetComposer(actorsFactory, keywordsDatabase) ! StartAcquisition(dataLabel)
  }

  private def startAcquisitionReply(dataLabel: DataLabel) {
    started += dataLabel
    eventLogger.end(dataLabel, OBS_START_ACQ)

    //check if the keyword recollection was performed on time
    eventLogger.check(dataLabel, OBS_START_ACQ, collectDeadline) match {
      case onTime: Boolean => if (!onTime) LOG.severe("Dataset " + dataLabel + ", Event " + OBS_START_ACQ + ", didn't finish on time")
      case _ => LOG.severe("Performance monitoring module failed to respond")
    }
  }

  private def endAcquisition(dataLabel: DataLabel) {
    eventLogger.start(dataLabel, OBS_END_ACQ)
    new KeywordSetComposer(actorsFactory, keywordsDatabase) ! EndAcquisition(dataLabel)
  }

  private def endAcquisitionReply(dataLabel: DataLabel) {
    ended += dataLabel
    eventLogger.end(dataLabel, OBS_END_ACQ)

    //check if the keyword recollection was performed on time
    eventLogger.check(dataLabel, OBS_END_ACQ, collectDeadline) match {
      case onTime: Boolean => if (!onTime) LOG.severe("Dataset " + dataLabel + ", Event " + OBS_END_ACQ + ", didn't finish on time")
      case _ => LOG.severe("Performance monitoring module failed to respond")
    }
  }

  private def endWrite(dataLabel: DataLabel) {
    eventLogger.start(dataLabel, OBS_END_DSET_WRITE)
    if (prepared.contains(dataLabel) && started.contains(dataLabel) && ended.contains(dataLabel)) {
      prepared -= dataLabel
      started -= dataLabel
      ended -= dataLabel
      try {
        updateFITSFile(dataLabel)
      } catch {
        case ex: FileNotFoundException => LOG.log(Level.SEVERE, ex.getMessage, ex)

      }
      keywordsDatabase ! Clean(dataLabel)
      eventLogger.end(dataLabel, OBS_END_DSET_WRITE)

    } else {
      LOG.severe("Dataset " + dataLabel + " ended writing dataset but never ended acquisition")
    }
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

  private def updateFITSFile(dataLabel: DataLabel): Unit = {
    val list = (keywordsDatabase !? Retrieve(dataLabel)).asInstanceOf[Option[List[Header]]]
    list match {
      case Some(headersList) => new FitsUpdater(new File("/tmp"), dataLabel, headersList).updateFitsHeaders()
      case None =>
    }
  }

}