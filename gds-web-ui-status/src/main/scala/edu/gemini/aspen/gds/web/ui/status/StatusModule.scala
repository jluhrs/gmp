package edu.gemini.aspen.gds.web.ui.status

import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import edu.gemini.aspen.gds.observationstate.ObservationStateProvider
import com.vaadin.Application
import edu.gemini.aspen.giapi.status.{StatusDatabaseService}
import com.vaadin.data.util.ObjectProperty
import edu.gemini.aspen.gds.api.Conversions._
import com.vaadin.ui.{Accordion, Panel, GridLayout, Label, Component}
import com.vaadin.terminal.ThemeResource

class StatusModule(statusDB: StatusDatabaseService, obsState: ObservationStateProvider) extends GDSWebModule {
  val title: String = "Status"
  val order: Int = 0
  val topGrid = new GridLayout(2, 3)
  val nLast = 10
  val accordion = new Accordion()
  val bottomPanel = new Panel("<b>Last " + nLast + " Observations</b>")
  val propertySources = new PropertyValuesHelper(statusDB, obsState)

  //labels
  val status = new Label()
  status.setContentMode(Label.CONTENT_XHTML)
  val processing = new Label()
  val lastDataLabel = new Label()

  //properties

  import StatusModule._

  val statusProp = new ObjectProperty(defaultStatus)
  status.setPropertyDataSource(statusProp)
  val processingProp = new ObjectProperty(defaultProcessing)
  processing.setPropertyDataSource(processingProp)
  val lastDataLabelProp = new ObjectProperty(defaultLastDataLabel)
  lastDataLabel.setPropertyDataSource(lastDataLabelProp)

  class Entry {
    var dataLabel = ""
    var times: String = ""
    var missing = ""
    var errors = ""
  }

  val lastDataLabels = new Array[Entry](nLast)
  for (i <- 0 until nLast) {
    lastDataLabels(i) = new Entry()
  }

  private def updateLastObservations(entries: Traversable[Entry], dataLabels: Traversable[String]) {
    if (entries.isEmpty) return
    val entry = entries.head
    if (dataLabels.isEmpty) {
      entry.dataLabel = ""
      entry.times = ""
      entry.missing = ""
      entry.errors = ""
      updateLastObservations(entries.tail, dataLabels)
      return
    }

    val dataLabel = dataLabels.head
    entry.dataLabel = dataLabel
    entry.times = propertySources.getTimes(dataLabel)
    if (propertySources.isInError(dataLabel)) {
      entry.missing = propertySources.getMissingKeywords(dataLabel)
      entry.errors = propertySources.getKeywordsInError(dataLabel)
    } else {
      entry.missing = ""
      entry.errors = ""
    }
    updateLastObservations(entries.tail, dataLabels.tail)
  }

  override def buildTabContent(app: Application): Component = {
    statusProp.setValue(propertySources.getStatus)
    processingProp.setValue(propertySources.getProcessing)
    lastDataLabelProp.setValue(propertySources.getLastDataLabel)

    topGrid.setMargin(true)
    topGrid.setSpacing(true)
    topGrid.setSizeFull()
    topGrid.setColumnExpandRatio(0, 1.0f)
    topGrid.setColumnExpandRatio(1, 3.0f)

    topGrid.addComponent(new Label("<b>Current Status:</b>", Label.CONTENT_XHTML))
    topGrid.addComponent(status)
    topGrid.addComponent(new Label("<b>DataSets in Process:</b>", Label.CONTENT_XHTML))
    topGrid.addComponent(processing)
    topGrid.addComponent(new Label("<b>Last DataSet:</b>", Label.CONTENT_XHTML))
    topGrid.addComponent(lastDataLabel)

    updateLastObservations(lastDataLabels, propertySources.getLastDataLabels(nLast))

    accordion.setSizeFull()
    generateAccordion()

    bottomPanel.addComponent(accordion)
    bottomPanel.setSizeFull()
    val panel = new Panel()
    panel.setSizeFull()
    panel.addComponent(topGrid)
    panel.addComponent(bottomPanel)
    panel
  }

  private def generateAccordion() {
    for (entry: Entry <- lastDataLabels) {
      if (!entry.dataLabel.equals("")) {
        if (propertySources.isInError(entry.dataLabel)) {
          val grid = new GridLayout(2, 3)
          grid.setMargin(true)
          grid.setSpacing(true)
          grid.setSizeFull()
          grid.setColumnExpandRatio(0, 1.0f)
          grid.setColumnExpandRatio(1, 3.0f)
          grid.addComponent(new Label("<b>Time to update FITS for last DataSet:</b>", Label.CONTENT_XHTML))
          grid.addComponent(new Label(entry.times))
          grid.addComponent(new Label("<b>Missing Keywords from last DataSet:</b>", Label.CONTENT_XHTML))
          grid.addComponent(new Label(entry.missing))
          grid.addComponent(new Label("<b>Error Collecting Keywords from last DataSet:</b>", Label.CONTENT_XHTML))
          grid.addComponent(new Label(entry.errors))
          accordion.addTab(grid, entry.dataLabel, new ThemeResource("../runo/icons/16/cancel.png"))
        }
        else {
          val grid = new GridLayout(2, 1)
          grid.setMargin(true)
          grid.setSpacing(true)
          grid.setSizeFull()
          grid.setColumnExpandRatio(0, 1.0f)
          grid.setColumnExpandRatio(1, 3.0f)
          grid.addComponent(new Label("<b>Time to update FITS for last DataSet:</b>", Label.CONTENT_XHTML))
          grid.addComponent(new Label(entry.times))
          accordion.addTab(grid, entry.dataLabel, new ThemeResource("../runo/icons/16/ok.png"))
        }
      }
    }
  }

  override def refresh(app: Application) {
    statusProp.setValue(propertySources.getStatus)
    processingProp.setValue(propertySources.getProcessing)
    lastDataLabelProp.setValue(propertySources.getLastDataLabel)

    updateLastObservations(lastDataLabels, propertySources.getLastDataLabels(nLast))

    accordion.removeAllComponents()
    generateAccordion()
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