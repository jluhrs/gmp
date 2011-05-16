package edu.gemini.aspen.gds.staticheaderreceiver

import edu.gemini.aspen.giapi.data.{DataLabel, FitsKeyword}
import org.apache.felix.ipojo.annotations.{Component, Instantiate, Provides}
import actors.Actor

trait TemporarySeqexecKeywordsDatabase extends Actor

@Component
@Instantiate
@Provides(specifications = Array(classOf[TemporarySeqexecKeywordsDatabase]))
class TemporarySeqexecKeywordsDatabaseImpl extends TemporarySeqexecKeywordsDatabase {

  start()

  def act() {
    loop {
      react {
        case StoreKeyword(dataLabel, key, value) => store(dataLabel, key, value)
        case RetrieveValue(dataLabel, key) => sender ! retrieveValue (dataLabel, key)
        case Clean(dataLabel) => clean(dataLabel)
        case CleanAll() => cleanAll()
        case x:Any => throw new RuntimeException("Argument not known " + x)
      }
    }
  }


  private val map = collection.mutable.Map.empty[DataLabel, collection.mutable.Map[FitsKeyword, AnyRef]]

  private def cleanAll(){
    map.clear()
  }

  private def clean(dataLabel:DataLabel){
    map -= dataLabel
  }

  //todo: clean map. Empty maps are being left over for each datalabel.
  private def retrieveValue(dataLabel: DataLabel, keyword: FitsKeyword):Option[AnyRef] = {
    if (map.contains(dataLabel)){
      map(dataLabel).remove(keyword)
    }else{
      None
    }
  }

  private def store(dataLabel: DataLabel, keyword: FitsKeyword, value: AnyRef){
    if (!map.contains(dataLabel)){
      map += (dataLabel -> collection.mutable.Map.empty[FitsKeyword,AnyRef])
    }
    map(dataLabel) += (keyword -> value)
  }
}
