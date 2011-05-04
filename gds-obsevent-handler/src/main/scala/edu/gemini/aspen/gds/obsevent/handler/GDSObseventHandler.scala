package edu.gemini.aspen.gds.obsevent.handler

import edu.gemini.aspen.giapi.data.ObservationEvent._
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel, ObservationEventHandler}
import org.apache.felix.ipojo.annotations.{Requires, Provides, Instantiate, Component}
import edu.gemini.aspen.gds.keywordssets.factory.CompositeActorsFactory
import scala.actors.Actor._
import actors.Actor
import edu.gemini.aspen.gds.keywordssets._
import edu.gemini.aspen.gds.keywords.database.{RetrieveAll, KeywordsDatabase}

/**
 * Simple Observation Event Handler that creates a KeywordSetComposer and launches the
 * keyword values acquisition process
 */
@Component
@Instantiate
@Provides(specifications = Array(classOf[ObservationEventHandler]))
class GDSObseventHandler(@Requires actorsFactory: CompositeActorsFactory, @Requires keywordsDatabase: KeywordsDatabase) extends ObservationEventHandler {
  private val replyHandler = new ReplyHandler(actorsFactory, keywordsDatabase)

  def onObservationEvent(event: ObservationEvent, dataLabel: DataLabel) {
    event match {
      case OBS_START_ACQ => replyHandler ! StartAcquisition(dataLabel)
      case OBS_END_ACQ => replyHandler ! EndAcquisition(dataLabel)
      case x: Any => throw new RuntimeException("Argument not known " + x)
    }
  }


}

class ReplyHandler(actorsFactory: CompositeActorsFactory, keywordsDatabase: KeywordsDatabase) extends Actor {
  start

  def act() {
    loop {
      react {
        case StartAcquisition(dataLabel) => startAcquisition(dataLabel)
        case StartAcquisitionReply(dataLabel) => startAcquisitionReply(dataLabel)
        case EndAcquisition(dataLabel) => endAcquisition(dataLabel)
        case EndAcquisitionReply(dataLabel) => endAcquisitionReply(dataLabel)
        case x: Any => throw new RuntimeException("Argument not known " + x)
      }
    }
  }

  private var started: Set[DataLabel] = Set[DataLabel]()

  private def startAcquisition(dataLabel: DataLabel) {
    new KeywordSetComposer(actorsFactory, keywordsDatabase) ! StartAcquisition(dataLabel)
  }

  private def startAcquisitionReply(dataLabel: DataLabel) {
    started += dataLabel

  }

  private def endAcquisition(dataLabel: DataLabel) {
    new KeywordSetComposer(actorsFactory, keywordsDatabase) ! EndAcquisition(dataLabel)
  }

  private def endAcquisitionReply(dataLabel: DataLabel) {
    if (started.contains(dataLabel)) {
      started -= dataLabel
      //add FITS file update here
      println(keywordsDatabase !? RetrieveAll(dataLabel))
    } else {
      throw new RuntimeException("Dataset " + dataLabel + " ended but never started")
    }
  }


}