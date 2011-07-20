package edu.gemini.aspen.gds.web.ui.status

import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import edu.gemini.aspen.gds.observationstate.ObservationStateProvider
import com.vaadin.Application
import com.vaadin.ui.{Alignment, GridLayout, Label, Component}
import org.scala_tools.time.Imports._
import edu.gemini.aspen.giapi.status.{Health, StatusItem, StatusDatabaseService}

class StatusModule(statusDB: StatusDatabaseService, obsState: ObservationStateProvider) extends GDSWebModule {
    val title: String = "Status"
    val order: Int = 1
    val (x, y) = (4, 6)
    val mainGrid = new GridLayout(x, y)

    val defaultLastDataLabel = "S20110501S00002"
    val defaultStatus = "UNKNOWN"
    val defaultTimes = "Set((OBS_PREP, PT0.104S), (OBS_START_ACQ, PT0.104S), (OBS_END_ACQ, PT0.104S), (OBS_START_READOUT, PT0.104S), (OBS_END_READOUT, PT0.104S), (OBS_START_DSET_WRITE, PT0.104S), (OBS_END_DSET_WRITE, PT0.104S), (FITS update, PT0.104S))"
    val defaultProcessing = "Set()"
    val defaultMissing = List("TEST1", "TEST2", "TEST3", "TEST4", "TEST5").toString
    val defaultErrors = List("TEST1", "TEST2", "TEST3").toString

    private var status: Label = _
    private var processing: Label = _
    private var lastDataLabel: Label = _
    private var times: Label = _
    private var missing: Label = _
    private var errors: Label = _

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

    private[status] def getStatus = {
        new Label(statusDB.getStatusItem("gpi:gds:health") match {
            case x: StatusItem[_] => x.getValue.toString
            case _ => defaultStatus
        })
    }

    private[status] def getLastDataLabel = {
        new Label(obsState.getLastDataLabel map {
            _.getName
        } getOrElse defaultLastDataLabel)
    }

    private[status] def getTimes = {
        new Label(obsState.getLastDataLabel map {
            obsState.getTimes(_) map {
                case (x: AnyRef, y: Option[Duration]) => (x, y.getOrElse(""))
            } toString
        } getOrElse defaultTimes)
    }

    private[status] def getProcessing = {
        new Label(obsState.getObservationsInProgress.toString())
    }

    private[status] def getMissingKeywords = {
        new Label(obsState.getLastDataLabel map {
            obsState.getMissingKeywords(_).toString()
        } getOrElse defaultMissing)
    }

    private[status] def getKeywordsInError = {
        new Label(obsState.getLastDataLabel map {
            obsState.getKeywordsInError(_).toString()
        } getOrElse defaultErrors)
    }

    //todo: change defaults to empty Strings
    //todo: Change output. toString might not be the best way to present the data

    private def updateKeywords() {
        val newMissing = getMissingKeywords
        mainGrid.replaceComponent(missing, newMissing)
        missing = newMissing

        val newErrors = getKeywordsInError
        mainGrid.replaceComponent(errors, newErrors)
        errors = newErrors
    }

}