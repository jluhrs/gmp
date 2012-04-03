package edu.gemini.aspen.gds.web.ui.logs

import java.util.logging.Logger
import model.{LogsContainer, LogSourceQueryDefinition, LoggingEventBeanQuery}
import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import edu.gemini.aspen.giapi.web.ui.vaadin._
import edu.gemini.aspen.giapi.web.ui.vaadin.layouts._
import edu.gemini.aspen.giapi.web.ui.vaadin.selects._
import com.vaadin.Application
import org.vaadin.addons.lazyquerycontainer._
import org.vaadin.henrik.drawer.Drawer
import com.vaadin.ui.{Alignment, Component}
import com.vaadin.data.Container
import com.vaadin.data.util.filter.Compare
import edu.gemini.aspen.giapi.web.ui.vaadin.components.Label

/**
 * The LogsModule class shows the content of GDS related log files */
class LogsModule(logSource: LogSource) extends GDSWebModule {
  val LOG = Logger.getLogger(this.getClass.getName)
  val STYLES = Map("WARN" -> "warn", "ERROR" -> "error")

  val title: String = "Logs"
  val order: Int = 1

  val dataContainer = buildDataContainer()
  val logTable = new Table(dataSource = dataContainer,
    selectable = true,
    style = "logs",
    sizeFull = true,
    sortAscending = true,
    sortPropertyId = "timeStamp",
    cellStyleGenerator = styleGenerator)
  val levelCombo = new NativeSelect(nullSelectionAllowed = false, action = changeLevel, immediate = true)
  levelCombo.addItem("INFO")
  levelCombo.addItem("WARN")
  levelCombo.addItem("ERROR")
  levelCombo.setValue("INFO")

  override def buildTabContent(app: Application): Component = new VerticalLayout(sizeFull = true) {
    val filters = new HorizontalLayout(spacing = true) {
      add(new Label("Level:"))
      add(levelCombo)
    }
    val drawer = new Drawer("Filters", filters)
    drawer.setWidth(100 percent)
    add(drawer)
    add(logTable, ratio = 1.0f)
  }

  override def refresh(app: Application) {
    dataContainer.refresh()
  }

  private def buildDataContainer() = {
    val queryFactory = new BeanQueryFactory[LoggingEventBeanQuery](classOf[LoggingEventBeanQuery])
    val definition = new LogSourceQueryDefinition(logSource, false, 300)

    definition.addProperty("timeStamp", classOf[java.lang.Long], 0L, true, true)
    definition.addProperty("level", classOf[String], "", true, true)
    definition.addProperty("loggerName", classOf[String], "", true, true)
    definition.addProperty("message", classOf[String], "", true, true)
    queryFactory.setQueryDefinition(definition)

    new LogsContainer(definition, queryFactory)
  }

  /**
   * Define a custom cell style based on the content of the cell */
  private def styleGenerator(itemId: AnyRef, propertyId: AnyRef): String = {
    STYLES.getOrElse(dataContainer.getItem(itemId).getItemProperty("level").toString, "")
  }

  def changeLevel(e:com.vaadin.data.Property.ValueChangeEvent) {
    dataContainer.removeAllContainerFilters()
    dataContainer.addContainerFilter(new Compare.Equal("level", e.getProperty.getValue))
    dataContainer.refresh()
  }
}