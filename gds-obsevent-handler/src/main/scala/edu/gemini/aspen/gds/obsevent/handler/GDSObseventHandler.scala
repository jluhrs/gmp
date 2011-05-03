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
    def onObservationEvent(event: ObservationEvent, dataLabel: DataLabel) {
        event match {
            case OBS_START_ACQ => startAcquisition(dataLabel)
            case OBS_END_ACQ => endAcquisition(dataLabel)
            case _ =>
        }
    }

    private def startAcquisition(dataLabel: DataLabel) {
        actor{
          ReplyHandler ! (new KeywordSetComposer(actorsFactory, keywordsDatabase) !? StartAcquisition(dataLabel))

        }
    }

    private def endAcquisition(dataLabel: DataLabel) {
        actor{
          ReplyHandler ! ((new KeywordSetComposer(actorsFactory, keywordsDatabase) !? EndAcquisition(dataLabel)), keywordsDatabase)

        }
    }
}

object ReplyHandler extends Actor{
  start
  def act(){
    loop{
      react{
        case StartAcquisitionReply(dataLabel) => startAcquisition(dataLabel)
        case (EndAcquisitionReply(dataLabel), keywordsDatabase:KeywordsDatabase) => endAcquisition(dataLabel,keywordsDatabase)
        case x:Any => throw new RuntimeException("Argument not known " + x)
      }
    }
  }
  private var started:Set[DataLabel] = Set[DataLabel]()
  private def startAcquisition(dataLabel:DataLabel){
    started += dataLabel
  }
  private def endAcquisition(dataLabel:DataLabel, keywordsDatabase:KeywordsDatabase){
    if(started.contains(dataLabel)){
      started -= dataLabel
      //add FITS file update here
      println(keywordsDatabase !? RetrieveAll(dataLabel))
    }else{
      throw new RuntimeException("Dataset "+dataLabel+" ended but never started")
    }
  }
}