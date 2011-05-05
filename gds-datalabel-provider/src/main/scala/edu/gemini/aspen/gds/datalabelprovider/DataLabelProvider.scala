package edu.gemini.aspen.gds.datalabelprovider

import edu.gemini.aspen.giapi.data.DataLabel

trait DataLabelProvider {
  def getDataLabel(): DataLabel
}

class DataLabelProviderImpl extends DataLabelProvider {

  def getDataLabel(): DataLabel = {
    //connect to DHS and ask for a data label
    new DataLabel("S20110505B0001")
  }

}