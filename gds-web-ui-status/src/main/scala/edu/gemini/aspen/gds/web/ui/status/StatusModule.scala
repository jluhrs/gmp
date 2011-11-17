package edu.gemini.aspen.gds.web.ui.status

import com.vaadin.Application
import com.vaadin.data.util.ObjectProperty
import com.vaadin.ui.{Accordion, Panel, GridLayout, Component}
import com.vaadin.terminal.ThemeResource
import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import edu.gemini.aspen.gds.observationstate.ObservationStateProvider
import edu.gemini.aspen.giapi.status.StatusDatabaseService
import edu.gemini.aspen.gds.api.Conversions._
import edu.gemini.aspen.giapi.web.ui.vaadin._
import collection.mutable.Stack
import annotation.tailrec

class StatusModule(statusDB: StatusDatabaseService, obsState: ObservationStateProvider) extends GDSWebModule {
  val title: String = "Status"
  val order: Int = 0
  val topGrid = new GridLayout(2, 3)
  val nLast = 10
  val accordion = new Accordion()
  val bottomPanel = new Panel("Last " + nLast + " Observations")
  val propertySources = new PropertyValuesHelper(statusDB, obsState)

  //labels
  val status = new Label(style = "gds-green")
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

  case class Entry(dataLabel: String = "", times: String = "", missing: String = "", errors: String = "")

  var lastDataLabels = List[Entry]()

  private def updateLastObservations(dataLabels: Traversable[String]) {
    lastDataLabels = dataLabels map {
      l => if (propertySources.isInError(l)) {
        new Entry(l, propertySources.getTimes(l), propertySources.getMissingKeywords(l), propertySources.getKeywordsInError(l))
      } else {
        new Entry(l, propertySources.getTimes(l))
      }
    } take(nLast) toList
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

    topGrid.addComponent(buildLabel("Current Status:"))
    topGrid.addComponent(status)
    topGrid.addComponent(buildLabel("DataSets in Process:"))
    topGrid.addComponent(processing)
    topGrid.addComponent(buildLabel("Last DataSet:"))
    topGrid.addComponent(lastDataLabel)

    updateLastObservations(propertySources.getLastDataLabels(nLast))

    accordion.setSizeFull()
    generateAccordion(app)

    bottomPanel.addComponent(accordion)
    bottomPanel.setSizeFull()
    val panel = new Panel()
    panel.setSizeFull()
    panel.addComponent(topGrid)
    panel.addComponent(bottomPanel)
    panel
  }

  private def generateAccordion(app: Application) {
    for (entry: Entry <- lastDataLabels) {
        val grid = new GridLayout(2, 3)
        grid.setMargin(true)
        grid.setSpacing(true)
        grid.setSizeFull()
        grid.setColumnExpandRatio(0, 1.0f)
        grid.setColumnExpandRatio(1, 3.0f)
        grid.addComponent(buildLabel("Time to update FITS for last DataSet"))
        grid.addComponent(new Label(entry.times))
        if (entry.missing.length() > 0) {
          grid.addComponent(buildLabel("Missing Keywords from last DataSet"))
          grid.addComponent(new Label(entry.missing))
        }
        if (entry.errors.length() > 0) {
          grid.addComponent(buildLabel("Error Collecting Keywords from last DataSet:"))
          grid.addComponent(new Label(entry.errors))
        }
        if (propertySources.isInError(entry.dataLabel)) {
          accordion.addTab(grid, entry.dataLabel, new ThemeResource("../gds/failed.png"))
        }
        else {
          accordion.addTab(grid, entry.dataLabel, new ThemeResource("../runo/icons/16/ok.png"))
        }
    }
    accordion.setVisible(accordion.getComponentCount != 0)
  }

  override def refresh(app: Application) {
    statusProp.setValue(propertySources.getStatus)
    status.setStyleName(propertySources.getStatusStyle)
    processingProp.setValue(propertySources.getProcessing)
    lastDataLabelProp.setValue(propertySources.getLastDataLabel)

    updateLastObservations(propertySources.getLastDataLabels(nLast))

    accordion.removeAllComponents()
    generateAccordion(app)
  }
}

protected object StatusModule {
  //default values
  val defaultStatus = "UNKNOWN"
  val defaultProcessing = ""
  val defaultLastDataLabel = ""
  val defaultTimes = ""
  val defaultMissing = ""
  val defaultErrors = ""

  def buildLabel(label: String) = new Label(caption = label, style = "gds-bold")
}