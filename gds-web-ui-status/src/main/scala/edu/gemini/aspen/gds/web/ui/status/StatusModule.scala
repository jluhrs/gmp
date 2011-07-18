package edu.gemini.aspen.gds.web.ui.status

import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import com.vaadin.ui.{Label, Panel, Component, Window}
import edu.gemini.aspen.giapi.status.StatusDatabaseService
import edu.gemini.aspen.gds.observationstate.ObservationStateProvider

class StatusModule(statusDB: StatusDatabaseService, obsState: ObservationStateProvider) extends GDSWebModule {
    val title: String = "Status"
    val order: Int = 1
    val mainPanel = new Panel()
    var status: Label = _
    var processing: Label = _
    var lastDataLabel: Label = _
    var times: Label = _

    override def buildTabContent(mainWindow: Window): Component = {
        status = new Label(statusDB.getStatusItem("gpi:gds:health").getValue.toString())
        processing = new Label(obsState.getObservationsInProgress.toString())
        lastDataLabel = new Label(obsState.getLastDataLabel map {
            _.getName
        } getOrElse "")
        times = new Label(obsState.getLastDataLabel map {
            obsState.getTimes(_).toString()
        } getOrElse "")

        mainPanel.setSizeFull
        mainPanel.addComponent(new Label("Current Status"))
        mainPanel.addComponent(status)
        mainPanel.addComponent(new Label("DataSets in Process"))
        mainPanel.addComponent(processing)
        mainPanel.addComponent(new Label("Last DataSet"))
        mainPanel.addComponent(lastDataLabel)
        mainPanel.addComponent(new Label("Time to Process Last DataSet"))
        mainPanel.addComponent(times)
        mainPanel
    }

    override def tabSelected() {
        updateHealth()
        updateObservationState()
    }

    private def updateHealth() {
        val newStatus = new Label(statusDB.getStatusItem("gpi:gds:health").getValue.toString())
        mainPanel.replaceComponent(status, newStatus)
        status = newStatus
    }

    private def updateObservationState() {
        val newProcessing = new Label(obsState.getObservationsInProgress.toString())
        mainPanel.replaceComponent(processing, newProcessing)
        processing = newProcessing

        val newLastDataLabel = new Label(obsState.getLastDataLabel map {
            _.getName
        } getOrElse "")
        mainPanel.replaceComponent(lastDataLabel, newLastDataLabel)
        lastDataLabel = newLastDataLabel

        val newTimes = new Label(obsState.getLastDataLabel map {
            obsState.getTimes(_).toString()
        } getOrElse "")
        mainPanel.replaceComponent(times, newTimes)
        times = newTimes
    }
}