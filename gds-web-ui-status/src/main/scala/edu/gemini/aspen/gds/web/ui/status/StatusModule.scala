package edu.gemini.aspen.gds.web.ui.status

import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import edu.gemini.aspen.giapi.status.StatusDatabaseService
import edu.gemini.aspen.gds.observationstate.ObservationStateProvider
import com.vaadin.Application
import com.vaadin.ui.{Alignment, GridLayout, Label, Component}

class StatusModule(statusDB: StatusDatabaseService, obsState: ObservationStateProvider) extends GDSWebModule {
    val title: String = "Status"
    val order: Int = 1
    val (x, y) = (4, 6)
    val mainGrid = new GridLayout(x, y)

    var status: Label = _
    var processing: Label = _
    var lastDataLabel: Label = _
    var times: Label = _
    var missing: Label = _
    var errors: Label = _

    override def buildTabContent(app: Application): Component = {
        status = getStatus
        processing = getProcessing
        lastDataLabel = getLastDataLabel
        times = getTimes
        missing = getMissingKeywords
        errors = getKeywordsInError

        //mainGrid.setSizeFull
        mainGrid.addComponent(new Label("Current Status: "), 0, 0)
        mainGrid.addComponent(status, 1, 0)
        mainGrid.addComponent(new Label("DataSets in Process: "), 0, 1)
        mainGrid.addComponent(processing, 1, 1, 3, 1)
        mainGrid.addComponent(new Label("Last DataSet: "), 0, 2)
        mainGrid.addComponent(lastDataLabel, 1, 2)
        mainGrid.addComponent(new Label("Time to Process last DataSet: "), 0, 3)
        mainGrid.addComponent(times, 1, 3, 3, 3)
        mainGrid.addComponent(new Label("Missing Keywords from last DataSet: "), 0, 4)
        mainGrid.addComponent(missing, 1, 4, 3, 4)
        mainGrid.addComponent(new Label("Error Collecting Keywords from last DataSet: "), 0, 5)
        mainGrid.addComponent(errors, 1, 5, 3, 5)

        //        for(j <- 0 until y){
        //            val c:Component=mainGrid.getComponent(0,j)
        //            mainGrid.setComponentAlignment(c,Alignment.MIDDLE_RIGHT)
        //        }

        mainGrid


    }

    override def tabSelected() {
        updateHealth()
        updateObservationState()
        updateKeywords()

    }

    private def updateHealth() {
        val newStatus = getStatus
        mainGrid.replaceComponent(status, newStatus)
        status = newStatus
    }

    private def updateObservationState() {
        val newProcessing = getProcessing
        mainGrid.replaceComponent(processing, newProcessing)
        processing = newProcessing

        val newLastDataLabel = getLastDataLabel
        mainGrid.replaceComponent(lastDataLabel, newLastDataLabel)
        lastDataLabel = newLastDataLabel

        val newTimes = getTimes
        mainGrid.replaceComponent(times, newTimes)
        times = newTimes
    }

    private def getStatus = {
        new Label(statusDB.getStatusItem("gpi:gds:health").getValue.toString())
    }

    private def getLastDataLabel = {
        new Label(obsState.getLastDataLabel map {
            _.getName
        } getOrElse "testing datalabel")
    }

    private def getTimes = {
        new Label(obsState.getLastDataLabel map {
            obsState.getTimes(_).toString()
        } getOrElse "tesint sldkfnsldf ;sdfjlsdfg times d;fj;lfsf s;dfjslk")
    }

    private def getProcessing = {
        new Label(obsState.getObservationsInProgress.toString())
    }

    private def getMissingKeywords = {
        new Label(obsState.getLastDataLabel map {
            obsState.getMissingKeywords(_).toString()
        } getOrElse "tesint sldkfnsldf ;sdfjlsdfg times d;fj;lfsf s;dfjslk")
    }

    private def getKeywordsInError = {
        new Label(obsState.getLastDataLabel map {
            obsState.getKeywordsInError(_).toString()
        } getOrElse "tesint sldkfnsldf ;sdfjlsdfg times d;fj;lfsf s;dfjslk")
    }

    private def updateKeywords() {
        val newMissing = getMissingKeywords
        mainGrid.replaceComponent(missing, newMissing)
        missing = newMissing

        val newErrors = getKeywordsInError
        mainGrid.replaceComponent(errors, newErrors)
        errors = newErrors
    }

}