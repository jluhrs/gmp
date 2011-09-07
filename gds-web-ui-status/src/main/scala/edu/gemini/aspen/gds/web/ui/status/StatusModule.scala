package edu.gemini.aspen.gds.web.ui.status

import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import edu.gemini.aspen.gds.observationstate.ObservationStateProvider
import com.vaadin.Application
import edu.gemini.aspen.giapi.status.{StatusDatabaseService}
import com.vaadin.data.util.ObjectProperty
import com.vaadin.ui.{Panel, GridLayout, Label, Component}
import edu.gemini.aspen.gds.api.Conversions._

class StatusModule(statusDB: StatusDatabaseService, obsState: ObservationStateProvider) extends GDSWebModule {
  val title: String = "Status"
  val order: Int = 0
  val topGrid = new GridLayout(2, 6)
  val nLast = 10
  val bottomGrid = new GridLayout(2, nLast + 1)
  val propertySources = new PropertyValuesHelper(statusDB, obsState)

  //labels
  val status = new Label()
  status.setContentMode(Label.CONTENT_XHTML)
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


  class Entry {
    val dataLabel = new Label()
    val dataLabelProp = new ObjectProperty(defaultLastDataLabel)
    dataLabel.setPropertyDataSource(dataLabelProp)

    val status = new Label()
    status.setContentMode(Label.CONTENT_XHTML)
    val statusProp = new ObjectProperty(defaultStatus)
    status.setPropertyDataSource(statusProp)

    val missing = new Label()
    val missingProp = new ObjectProperty(defaultMissing)
    missing.setPropertyDataSource(missingProp)

    val errors = new Label()
    val errorsProp = new ObjectProperty(defaultErrors)
    errors.setPropertyDataSource(errorsProp)
  }


  val lastDataLabels = new Array[Entry](nLast)
  for (i <- 0 until nLast) {
    lastDataLabels(i) = new Entry()
  }


  private def updateLastObservations(entries: Traversable[Entry], dataLabels: Traversable[String]) {
    if (entries.isEmpty) return
    val entry = entries.head
    if (dataLabels.isEmpty) {
      entry.dataLabelProp.setValue("")
      entry.statusProp.setValue("")
      entry.missingProp.setValue("")
      entry.errorsProp.setValue("")
      updateLastObservations(entries.tail, dataLabels)
      return
    }

    val dataLabel = dataLabels.head
    entry.dataLabelProp.setValue(dataLabel)
    if (propertySources.isInError(dataLabel)) {
      entry.statusProp.setValue("<span style=\"color: red\">ERROR</span>")
      entry.missingProp.setValue(propertySources.getMissingKeywords(dataLabel))
      entry.errorsProp.setValue(propertySources.getKeywordsInError(dataLabel))
    } else {
      entry.statusProp.setValue("<span style=\"color: green\">OK</span>")
      entry.missingProp.setValue("")
      entry.errorsProp.setValue("")
    }
    updateLastObservations(entries.tail, dataLabels.tail)
  }

  override def buildTabContent(app: Application): Component = {
    statusProp.setValue(propertySources.getStatus)
    processingProp.setValue(propertySources.getProcessing)
    lastDataLabelProp.setValue(propertySources.getLastDataLabel)
    timesProp.setValue(propertySources.getTimes)
    missingProp.setValue(propertySources.getMissingKeywords)
    errorsProp.setValue(propertySources.getKeywordsInError)

    topGrid.setMargin(true)
    topGrid.setSpacing(true)
    topGrid.setSizeFull()
    topGrid.addComponent(new Label("<b>Current Status:</b>", Label.CONTENT_XHTML))
    topGrid.addComponent(status)
    topGrid.addComponent(new Label("<b>DataSets in Process:</b>", Label.CONTENT_XHTML))
    topGrid.addComponent(processing)
    topGrid.addComponent(new Label("<b>Last DataSet:</b>", Label.CONTENT_XHTML))
    topGrid.addComponent(lastDataLabel)
    topGrid.addComponent(new Label("<b>Time to update FITS for last DataSet:</b>", Label.CONTENT_XHTML))
    topGrid.addComponent(times)
    topGrid.addComponent(new Label("<b>Missing Keywords from last DataSet:</b>", Label.CONTENT_XHTML))
    topGrid.addComponent(missing)
    topGrid.addComponent(new Label("<b>Error Collecting Keywords from last DataSet:</b>", Label.CONTENT_XHTML))
    topGrid.addComponent(errors)

    updateLastObservations(lastDataLabels, propertySources.getLastDataLabels(nLast))

    bottomGrid.setMargin(true)
    bottomGrid.setSpacing(true)
    //bottomGrid.setSizeFull()
    bottomGrid.addComponent(new Label("<b>DataLabel:</b>", Label.CONTENT_XHTML))
    bottomGrid.addComponent(new Label("<b>State:</b>", Label.CONTENT_XHTML))
    //    bottomGrid.addComponent(new Label("<b>Keywords in Error:</b>", Label.CONTENT_XHTML))
    //    bottomGrid.addComponent(new Label("<b>Missing Keywords:</b>", Label.CONTENT_XHTML))

    for (entry <- lastDataLabels) {
      bottomGrid.addComponent(entry.dataLabel)
      bottomGrid.addComponent(entry.status)
      //      bottomGrid.addComponent(entry.missing)
      //      bottomGrid.addComponent(entry.errors)
    }

    val panel = new Panel()
    panel.setSizeFull()
    panel.addComponent(topGrid)
    panel.addComponent(bottomGrid)
    panel
  }


  override def refresh() {
    statusProp.setValue(propertySources.getStatus)
    processingProp.setValue(propertySources.getProcessing)
    lastDataLabelProp.setValue(propertySources.getLastDataLabel)
    timesProp.setValue(propertySources.getTimes)
    missingProp.setValue(propertySources.getMissingKeywords)
    errorsProp.setValue(propertySources.getKeywordsInError)

    updateLastObservations(lastDataLabels, propertySources.getLastDataLabels(nLast))
  }


}

object StatusModule {
  //default values
  val defaultStatus = "UNKNOWN"
  val defaultProcessing = ""
  val defaultLastDataLabel = ""
  val defaultTimes = ""
  val defaultMissing = ""
  val defaultErrors = ""

}