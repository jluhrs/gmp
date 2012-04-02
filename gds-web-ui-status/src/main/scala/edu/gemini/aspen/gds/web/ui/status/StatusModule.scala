package edu.gemini.aspen.gds.web.ui.status

import com.vaadin.Application
import com.vaadin.data.util.ObjectProperty
import com.vaadin.ui.{Accordion, Component}
import com.vaadin.terminal.ThemeResource
import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import edu.gemini.aspen.gds.observationstate.ObservationStateProvider
import edu.gemini.aspen.giapi.status.StatusDatabaseService
import edu.gemini.aspen.gds.api.Conversions._
import edu.gemini.aspen.giapi.web.ui.vaadin.components._
import edu.gemini.aspen.giapi.web.ui.vaadin.containers.Panel
import edu.gemini.aspen.giapi.web.ui.vaadin.layouts._
import StatusModule._
import edu.gemini.aspen.gmp.top.Top
import edu.gemini.aspen.giapi.web.ui.vaadin.data.Property

class StatusModule(statusDB: StatusDatabaseService, obsState: ObservationStateProvider, top: Top) extends GDSWebModule {
  val title: String = "Status"
  val order: Int = 0
  val topGrid = new GridLayout(columns = 2, rows = 3, margin = true, spacing = true)
  val nLast = 10
  val accordion = new Accordion()
  val bottomPanel = new Panel("Last " + nLast + " Observations", sizeFull = true) {
    add(accordion)
  }
  val propertySources = new PropertyValuesHelper(statusDB, obsState, top)

  //properties
  val statusProp = Property(defaultStatus)
  val processingProp = Property(defaultProcessing)
  val lastDataLabelProp = Property(defaultLastDataLabel)

  //labels
  val status = new Label(style = "gds-green", property = statusProp)
  val processing = new Label(property = processingProp)
  val lastDataLabel = new Label(property = lastDataLabelProp)

  case class Entry(dataLabel: String = "", times: String = "", missing: String = "", errors: String = "")

  override def buildTabContent(app: Application): Component = {
    statusProp.setValue(propertySources.getStatus)
    processingProp.setValue(propertySources.getProcessing)
    lastDataLabelProp.setValue(propertySources.getLastDataLabel)

    topGrid.setSizeFull()
    topGrid.setColumnExpandRatio(0, 1.0f)
    topGrid.setColumnExpandRatio(1, 3.0f)

    topGrid.add(buildLabel("Current Status:"))
    topGrid.add(status)
    topGrid.add(buildLabel("DataSets in Process:"))
    topGrid.add(processing)
    topGrid.add(buildLabel("Last DataSet:"))
    topGrid.add(lastDataLabel)

    accordion.setSizeFull()
    generateAccordion(app, getLastEntries(propertySources.getLastDataLabels(nLast)))

    new Panel(sizeFull =true) {
      add(topGrid)
      add(bottomPanel)
    }
  }

  private def generateAccordion(app: Application, lastEntries: List[Entry]) {
    for (entry: Entry <- lastEntries) {
      val grid = new GridLayout(columns = 2, rows = 3, margin = true, spacing = true) {
        setSizeFull()
        setColumnExpandRatio(0, 1.0f)
        setColumnExpandRatio(1, 3.0f)
        add(buildLabel("Time to update FITS for last DataSet"))
        add(new Label(entry.times))
        if (entry.missing.length() > 0) {
          add(buildLabel("Missing Keywords from last DataSet"))
          add(new Label(entry.missing))
        }
        if (entry.errors.length() > 0) {
          add(buildLabel("Error Collecting Keywords from last DataSet:"))
          add(new Label(entry.errors))
        }
      }
      val resource = if (propertySources.isInError(entry.dataLabel)) {
        new ThemeResource("../gds/failed.png")
      } else {
        new ThemeResource("../runo/icons/16/ok.png")
      }
      accordion.addTab(grid, entry.dataLabel, resource)
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

  private def getLastEntries(dataLabels: Traversable[String]): List[Entry] = {
    dataLabels map {
      l => if (propertySources.isInError(l)) {
        new Entry(l, propertySources.getTimes(l), propertySources.getMissingKeywords(l), propertySources.getKeywordsInError(l))
      } else {
        new Entry(l, propertySources.getTimes(l))
      }
    } take (nLast) toList
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