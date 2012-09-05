package edu.gemini.aspen.gds.web.ui.status

import com.vaadin.Application
import com.vaadin.ui.Component
import com.vaadin.terminal.{Sizeable, ThemeResource}
import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import edu.gemini.aspen.gds.observationstate._
import edu.gemini.aspen.gds.api.Conversions._
import edu.gemini.aspen.giapi.web.ui.vaadin.components._
import edu.gemini.aspen.giapi.web.ui.vaadin.containers.{VerticalSplitPanel, HorizontalSplitPanel, Panel}
import edu.gemini.aspen.giapi.web.ui.vaadin.layouts._
import edu.gemini.aspen.giapi.web.ui.vaadin._
import model.{ObservationsSource, ObservationSourceQueryDefinition, ObservationsBeanQuery}
import StatusModule._
import edu.gemini.aspen.giapi.web.ui.vaadin.data.{Container, Property}
import org.vaadin.addons.lazyquerycontainer.{LazyQueryContainer, BeanQueryFactory}
import edu.gemini.aspen.giapi.web.ui.vaadin.selects.Table
import scala.collection.JavaConversions._
import com.github.wolfie.refresher.Refresher
import edu.gemini.aspen.gds.observationstate.ObservationInfo
import org.joda.time.DateTime
import com.vaadin.data.util.IndexedContainer
import edu.gemini.aspen.gds.api.{CollectionError, ErrorCollectedValue, CollectedValue}

class StatusModule(observationSource: ObservationsSource) extends GDSWebModule {
  val title: String = "Status"
  val order: Int = 0
  val nLast = 10

  //properties
  val statusProp = Property(defaultStatus)
  val processingProp = Property(defaultProcessing)
  val lastDataLabelProp = Property(defaultLastDataLabel)
  val statusProperty = "status"

  //labels
  val status = new Label(style = "gds-green", property = statusProp)
  val processing = new Label(property = processingProp)
  val lastDataLabel = new Label(property = lastDataLabelProp)

  // Grid for the elements on the top of the page
  val topGrid = new GridLayout(columns = 2, rows = 3, height = 100 px, width = 100 pct) {
    setColumnExpandRatio(0, 1.0f)
    setColumnExpandRatio(1, 3.0f)

    add(buildLabel("Current Health:"), 0, 0)
    add(status, 1, 0)
    add(buildLabel("DataSets in Process:"), 0, 1)
    add(processing, 1, 1)
    add(buildLabel("Last DataSet:"), 0, 2)
    add(lastDataLabel, 1, 2)
  }

  // Observations table
  val dataContainer = buildDataContainer()
  val statusTable = new Table(dataSource = dataContainer,
    selectable = true,
    style = "logs",
    sizeFull = true,
    sortAscending = true,
    sortPropertyId = "timeStamp",
    cellStyleGenerator = styleGenerator) {

    override def formatPropertyValue(rowId: AnyRef, colId: AnyRef, property: com.vaadin.data.Property): String = {
      val v = property.getValue()
      colId match {
        case "errorMsg" => v match {
          case Some(s: String) => s
          case _ => ""
        }
        case "writeTime" => v match {
          case Some(t: Long) => "%d [ms]".format(t)
          case _ => ""
        }
        case "timeStamp" => v match {
          case d: DateTime => d.toString
          case _ => ""
        }
        case _ => super.formatPropertyValue(rowId, colId, property)
      }
    }
  }
  // Generated column for the status icon
  statusTable.addGeneratedColumn(statusProperty, (itemId: AnyRef, columnId: AnyRef) => {
    val result = dataContainer.getItem(itemId).getItemProperty("result").getValue
    result match {
      case Successful => new Embedded(objectType = com.vaadin.ui.Embedded.TYPE_IMAGE, source = new ThemeResource("../runo/icons/16/ok.png"))
      case MissingKeywords => new Embedded(objectType = com.vaadin.ui.Embedded.TYPE_IMAGE, source = new ThemeResource("../gds/warning.png"))
      case ErrorKeywords => new Embedded(objectType = com.vaadin.ui.Embedded.TYPE_IMAGE, source = new ThemeResource("../gds/warning.png"))
      case ObservationError => new Embedded(objectType = com.vaadin.ui.Embedded.TYPE_IMAGE, source = new ThemeResource("../gds/failed.png"))
      case _ => new Embedded(objectType = com.vaadin.ui.Embedded.TYPE_IMAGE, source = new ThemeResource("../runo/icons/16/ok.png"))
    }
  })

  setColumnTitles()
  statusTable.setColumnAlignment(statusProperty, com.vaadin.ui.Table.ALIGN_CENTER)
  statusTable.setColumnWidth(statusProperty, 20)
  statusTable.setColumnWidth("keyword", 200)
  statusTable.addItemClickListener(e => {
    displayKeywords(e.getItemId)
  })

  val keywordsTable = new Table(sizeFull = true, style = "logs",
    cellStyleGenerator = keywordsStyleGenerator, selectable = true)
  keywordsTable.addContainerProperty("status", classOf[java.lang.String], "")
  keywordsTable.addContainerProperty("keyword", classOf[java.lang.String], "")
  keywordsTable.addContainerProperty("value", classOf[java.lang.String], "")
  keywordsTable.addGeneratedColumn(statusProperty, (itemId: AnyRef, columnId: AnyRef) => {
    val result = keywordsTable.getItem(itemId).getItemProperty("cv").getValue
    result match {
      case x: ErrorCollectedValue => new Embedded(objectType = com.vaadin.ui.Embedded.TYPE_IMAGE, source = new ThemeResource("../gds/failed.png"))
      case x => new Embedded(objectType = com.vaadin.ui.Embedded.TYPE_IMAGE, source = new ThemeResource("../runo/icons/16/ok.png"))
    }
  })
  keywordsTable.setColumnAlignment(statusProperty, com.vaadin.ui.Table.ALIGN_CENTER)
  keywordsTable.setColumnWidth(statusProperty, 20)
  keywordsTable.setVisibleColumns(StatusModule.KEYWORD_COLUMNS)
  setKeywordColumnTitles()

  def displayKeywords(itemId: AnyRef) = {
    statusTable.getItem(itemId).getItemProperty("collectedValues").getValue match {
      case l: List[CollectedValue[_]] => val items = l.zipWithIndex.collect {
        case (c:ErrorCollectedValue, i) => (i, Seq("cv" -> c, "keyword" -> c.keyword.key, "value" -> "-"))
        case (c:CollectedValue[_], i) => (i, Seq("cv" -> c, "keyword" -> c.keyword.key, "value" -> c.value.toString))
        }
      if (items.nonEmpty) {
        keywordsTable.setContainerDataSource(Container(items: _*))
        keywordsTable.setVisibleColumns(StatusModule.KEYWORD_COLUMNS)
      }
      case _ =>
    }
  }

  val bottomPanel = new Panel(sizeFull = true) {
    add(topGrid)
    add(new VerticalLayout(caption = "Observations", sizeFull = true) {
      add(statusTable)
    })
    add(new VerticalLayout(caption = "Keywords", sizeFull = true) {
      add(keywordsTable)
    })
    addComponent(new Refresher)
  }

  case class Entry(dataLabel: String = "", times: String = "", missing: String = "", errors: String = "")

  override def buildTabContent(app: Application): Component = {
    refresh()

    bottomPanel
  }

  override def refresh(app: Application) {
    refresh()
  }

  private def refresh() {
    observationSource.observations.headOption foreach {
      o => lastDataLabelProp.setValue(o.getDataLabel())
    }
    processingProp.setValue(observationSource.pending.mkString(", "))
    statusTable.refreshRowCache()
    statusTable.setContainerDataSource(statusTable.getContainerDataSource)
    statusTable.setVisibleColumns(StatusModule.OBSERVATION_COLUMNS)
  }

  private def buildDataContainer() = {
    val queryFactory = new BeanQueryFactory[ObservationsBeanQuery](classOf[ObservationsBeanQuery])
    val definition = new ObservationSourceQueryDefinition(observationSource, false, 300)

    definition.addProperty("result", classOf[ObservationInfo], Successful, true, true)
    definition.addProperty("timeStamp", classOf[java.lang.Long], 0L, true, true)
    definition.addProperty("dataLabel", classOf[String], "", true, true)
    definition.addProperty("errorMsg", classOf[String], "", true, true)
    definition.addProperty("writeTime", classOf[String], "", true, true)
    queryFactory.setQueryDefinition(definition)

    new LazyQueryContainer(definition, queryFactory)
  }

  /**
   * Define a custom cell style based on the content of the cell */
  private def styleGenerator(itemId: AnyRef, propertyId: AnyRef): String = {
    StatusModule.OBSERVATION_STYLES.getOrElse(dataContainer.getItem(itemId).getItemProperty("result").getValue.asInstanceOf[ObservationStatus], "")
  }

  /**
   * Define a custom cell style based on the content of the cell */
  private def keywordsStyleGenerator(itemId: AnyRef, propertyId: AnyRef): String = {
    keywordsTable.getItem(itemId).getItemProperty("cv").getValue match {
      case x: ErrorCollectedValue if x.error == CollectionError.ItemNotFound => "warn"
      case x: ErrorCollectedValue => "error"
      case x => ""
    }
  }

  /**
   * Define the headers for the observations table */
  private def setColumnTitles() {
    StatusModule.OBSERVATION_COLUMN_NAMES foreach {
      case (c, t) => statusTable.setColumnHeader(c, t)
    }
  }

  /**
   * Define the headers for the keywords table */
  private def setKeywordColumnTitles() {
    StatusModule.KEYWORD_COLUMN_NAMES foreach {
      case (c, t) => keywordsTable.setColumnHeader(c, t)
    }
  }

  observationSource.registerListener(() => {
    refresh()
  })

}

protected object StatusModule {
  //default values
  val defaultStatus = "UNKNOWN"
  val defaultProcessing = "-"
  val defaultLastDataLabel = "-"
  val defaultTimes = ""
  val defaultMissing = ""
  val defaultErrors = ""

  val OBSERVATION_STYLES = Map[ObservationStatus, String](MissingKeywords -> "warn", ObservationError -> "error", ErrorKeywords -> "warn")
  val OBSERVATION_COLUMN_NAMES = Map("status" -> "", "timeStamp" -> "Time ", "dataLabel" -> "Data Label", "errorMsg" -> "Error message", "writeTime" -> "Time to write")
  val OBSERVATION_COLUMNS = Array[AnyRef]("status", "timeStamp", "dataLabel", "writeTime", "errorMsg")

  val KEYWORD_STYLES = Map[CollectionError.Value, String](CollectionError.GenericError -> "error")
  val KEYWORD_COLUMN_NAMES = Map("status" -> "", "keyword" -> "Keyword", "value" -> "Value")
  val KEYWORD_COLUMNS = Array[AnyRef]("status", "keyword", "value")

  def buildLabel(label: String) = new Label(caption = label, style = "gds-bold")

}