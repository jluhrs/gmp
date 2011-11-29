package edu.gemini.aspen.gds.web.ui.status

import com.vaadin.Application
import com.vaadin.data.util.ObjectProperty
import com.vaadin.ui.{Accordion, Panel, GridLayout, Component}
import com.vaadin.terminal.ThemeResource
import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import edu.gemini.aspen.gds.observationstate.ObservationStateProvider
import edu.gemini.aspen.giapi.status.StatusDatabaseService
import edu.gemini.aspen.gds.api.Conversions._
import edu.gemini.aspen.giapi.web.ui.vaadin.components._
import StatusModule._

class StatusModule(statusDB: StatusDatabaseService, obsState: ObservationStateProvider) extends GDSWebModule {
  val title: String = "Status"
  val order: Int = 0
  val topGrid = new GridLayout(2, 3)
  val nLast = 10
  val accordion = new Accordion()
  val bottomPanel = new Panel("Last " + nLast + " Observations")
  val propertySources = new PropertyValuesHelper(statusDB, obsState)

  //properties
  val statusProp = new ObjectProperty(defaultStatus)
  val processingProp = new ObjectProperty(defaultProcessing)
  val lastDataLabelProp = new ObjectProperty(defaultLastDataLabel)

  //labels
  val status = new Label(style = "gds-green", property = statusProp)
  val processing = new Label(property = processingProp)
  val lastDataLabel = new Label(property = lastDataLabelProp)

  case class Entry(dataLabel: String = "", times: String = "", missing: String = "", errors: String = "")

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

    accordion.setSizeFull()
    generateAccordion(app, getLastEntries(propertySources.getLastDataLabels(nLast)))

    bottomPanel.addComponent(accordion)
    bottomPanel.setSizeFull()
    val panel = new Panel()
    panel.setSizeFull()
    panel.addComponent(topGrid)
    panel.addComponent(bottomPanel)
    panel
  }

  private def generateAccordion(app: Application, lastEntries:List[Entry]) {
    for (entry: Entry <- lastEntries) {
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

    accordion.removeAllComponents()
    generateAccordion(app, getLastEntries(propertySources.getLastDataLabels(nLast)))
  }

  private def getLastEntries(dataLabels: Traversable[String]):List[Entry] = {
    dataLabels map {
      l => if (propertySources.isInError(l)) {
        new Entry(l, propertySources.getTimes(l), propertySources.getMissingKeywords(l), propertySources.getKeywordsInError(l))
      } else {
        new Entry(l, propertySources.getTimes(l))
      }
    } take(nLast) toList
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