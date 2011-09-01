package edu.gemini.aspen.gds.web.ui.status

import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import edu.gemini.aspen.gds.observationstate.ObservationStateProvider
import com.vaadin.Application
import com.vaadin.ui.{GridLayout, Label, Component}
import edu.gemini.aspen.giapi.status.{StatusDatabaseService}
import com.vaadin.data.util.ObjectProperty

class StatusModule(statusDB: StatusDatabaseService, obsState: ObservationStateProvider) extends GDSWebModule {
  val title: String = "Status"
  val order: Int = 1
  val (x, y) = (4, 6)
  val mainGrid = new GridLayout(x, y)
  val propertySources = new PropertyValuesHelper(statusDB, obsState)

  //labels
  val status = new Label()
  val processing = new Label()
  val lastDataLabel = new Label()
  val times = new Label()
  val missing = new Label()
  val errors = new Label()

  //properties

  import StatusModule._

  val statusProp = new ObjectProperty(defaultStatus)
  status.setPropertyDataSource(statusProp)
  val processingProp = new ObjectProperty(defaultProcessing)
  processing.setPropertyDataSource(processingProp)
  val lastDataLabelProp = new ObjectProperty(defaultLastDataLabel)
  lastDataLabel.setPropertyDataSource(lastDataLabelProp)
  val timesProp = new ObjectProperty(defaultTimes)
  times.setPropertyDataSource(timesProp)
  val missingProp = new ObjectProperty(defaultMissing)
  missing.setPropertyDataSource(missingProp)
  val errorsProp = new ObjectProperty(defaultErrors)
  errors.setPropertyDataSource(errorsProp)

  override def buildTabContent(app: Application): Component = {
    statusProp.setValue(propertySources.getStatus)
    processingProp.setValue(propertySources.getProcessing)
    lastDataLabelProp.setValue(propertySources.getLastDataLabel)
    timesProp.setValue(propertySources.getTimes)
    missingProp.setValue(propertySources.getMissingKeywords)
    errorsProp.setValue(propertySources.getKeywordsInError)

    mainGrid.setMargin(true)
    mainGrid.setSpacing(true)
    mainGrid.addComponent(new Label("<b>Current Status:</b>", Label.CONTENT_XHTML), 0, 0)
    mainGrid.addComponent(status, 1, 0)
    mainGrid.addComponent(new Label("<b>DataSets in Process:</b>", Label.CONTENT_XHTML), 0, 1)
    mainGrid.addComponent(processing, 1, 1, 3, 1)
    mainGrid.addComponent(new Label("<b>Last DataSet:</b>", Label.CONTENT_XHTML), 0, 2)
    mainGrid.addComponent(lastDataLabel, 1, 2)
    mainGrid.addComponent(new Label("<b>Time to Process last DataSet:</b>", Label.CONTENT_XHTML), 0, 3)
    mainGrid.addComponent(times, 1, 3, 3, 3)
    mainGrid.addComponent(new Label("<b>Missing Keywords from last DataSet:</b>", Label.CONTENT_XHTML), 0, 4)
    mainGrid.addComponent(missing, 1, 4, 3, 4)
    mainGrid.addComponent(new Label("<b>Error Collecting Keywords from last DataSet:</b>", Label.CONTENT_XHTML), 0, 5)
    mainGrid.addComponent(errors, 1, 5, 3, 5)

    mainGrid
  }

  override def refresh() {
    statusProp.setValue(propertySources.getStatus)
    processingProp.setValue(propertySources.getProcessing)
    lastDataLabelProp.setValue(propertySources.getLastDataLabel)
    timesProp.setValue(propertySources.getTimes)
    missingProp.setValue(propertySources.getMissingKeywords)
    errorsProp.setValue(propertySources.getKeywordsInError)
  }


  //todo: change defaults to empty Strings
  //todo: Change output. toString might not be the best way to present the data


}

object StatusModule {
  //default values
  val defaultStatus = "UNKNOWN"
  val defaultProcessing = "Set()"
  val defaultLastDataLabel = "S20110501S00002"
  val defaultTimes = "Set((OBS_PREP, PT0.104S), (OBS_START_ACQ, PT0.104S), (OBS_END_ACQ, PT0.104S), (OBS_START_READOUT, PT0.104S), (OBS_END_READOUT, PT0.104S), (OBS_START_DSET_WRITE, PT0.104S), (OBS_END_DSET_WRITE, PT0.104S), (FITS update, PT0.104S))"
  val defaultMissing = List("TEST1", "TEST2", "TEST3", "TEST4", "TEST5").toString
  val defaultErrors = List("TEST1", "TEST2", "TEST3").toString

}