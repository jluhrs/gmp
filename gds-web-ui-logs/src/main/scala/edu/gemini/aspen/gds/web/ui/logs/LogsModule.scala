package edu.gemini.aspen.gds.web.ui.logs

import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import com.vaadin.Application
import model.{LogsContainer, LogSourceQueryDefinition, LoggingEventBeanQuery}
import scala.collection.JavaConversions._
import org.vaadin.addons.lazyquerycontainer._
import java.util.logging.{Level, Logger}
import com.vaadin.terminal.ThemeResource
import com.vaadin.ui.Table.{ColumnGenerator, CellStyleGenerator}
import com.vaadin.ui.themes.BaseTheme
import com.vaadin.event.ItemClickEvent
import edu.gemini.aspen.gds.web.ui.api.Preamble._
import com.vaadin.ui._
import com.vaadin.data.Container.{Filter, Filterable}
import com.vaadin.data.{Property, Container}
import com.vaadin.data.util.filter.Compare

class LogsModule(logSource: LogSource) extends GDSWebModule {
  val LOG = Logger.getLogger(this.getClass.getName)
  val title: String = "Logs"
  val order: Int = 2
  val logTable = new Table()
  val styleGenerator = new CellStyleGenerator {
    val styles = Map("WARN" -> "warn", "ERROR" -> "error")

    def getStyle(itemId: AnyRef, propertyId: AnyRef) = {
      styles.getOrElse(container.getItem(itemId).getItemProperty("level").toString, "")
    }
  }
  val expandIcon = new ThemeResource("../runo/icons/16/arrow-right.png")
  val expandTooltip = "Show stack trace"
  val container = {
    val queryFactory = new BeanQueryFactory[LoggingEventBeanQuery](classOf[LoggingEventBeanQuery])
    val definition = new LogSourceQueryDefinition(logSource, false, 100)

    definition.addProperty("timeStamp", classOf[java.lang.Long], 0L, true, true)
    definition.addProperty("level", classOf[String], "", true, true)
    definition.addProperty("loggerName", classOf[String], "", true, true)
    definition.addProperty("message", classOf[String], "", true, true)
    definition.addProperty("stackTrace", classOf[Label], "", true, false)
    queryFactory.setQueryDefinition(definition);

    new LogsContainer(definition, queryFactory)
  }

  override def buildTabContent(app: Application): Component = {
    LOG.warning("Build module")
    LOG.log(Level.SEVERE, "Error module", new RuntimeException())

    logTable.setContainerDataSource(container)
    logTable.setSelectable(true)
    logTable.setSizeFull()
    logTable.addStyleName("logs")
    logTable.setColumnReorderingAllowed(true)

    logTable.setCellStyleGenerator(styleGenerator)
    logTable.setColumnHeader("stackTrace", "")
    logTable.addGeneratedColumn("stackTrace", new ColumnGenerator {
      def generateCell(source: Table, itemId: AnyRef, columnId: AnyRef) = {
        if (!container.getItem(itemId).getItemProperty("stackTrace").toString.isEmpty) {
          val stackTraceButton = new Button("")
          stackTraceButton.setStyleName(BaseTheme.BUTTON_LINK)
          stackTraceButton.setIcon(expandIcon)
          stackTraceButton.setDescription(expandTooltip)
          stackTraceButton.addListener((e: Button#ClickEvent) => {
          })
          stackTraceButton
        } else {
          new Label()
        }
      }
    })

    val layout = new VerticalLayout
    layout.setSizeFull()
    val filterPanel = new Panel()
    val levelSelect = new NativeSelect()
    levelSelect.setCaption("Level")
    levelSelect.setInvalidAllowed(false)
    levelSelect.setImmediate(true)

    levelSelect.addItem("ALL")
    levelSelect.addItem("INFO")
    levelSelect.addItem("WARN")
    levelSelect.addItem("ERROR")
    levelSelect.addListener((e:Property.ValueChangeEvent) => {
      container.removeAllContainerFilters()
      e.getProperty.toString match {
        case "ALL" =>
        case x => {println("add filter " + x);container.addContainerFilter(new Compare.Equal("level", x))}
      }
      logTable.setContainerDataSource(container)
    })

    filterPanel.addComponent(levelSelect)

    layout.addComponent(filterPanel)
    layout.addComponent(logTable)
    layout.setExpandRatio(logTable, 1.0f)
    layout
  }
}
