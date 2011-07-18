package edu.gemini.aspen.gds.web.ui.keywords

import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import edu.gemini.aspen.gds.api.configuration.GDSConfigurationService
import edu.gemini.aspen.gds.web.ui.api.VaadinUtilities._
import com.vaadin.ui.Window.Notification
import com.jensjansson.pagedtable.PagedTable
import model.{ReadOnlyGDSKeywordsDataSource, GDSKeywordsDataSource}
import scala.collection.JavaConversions._
import com.vaadin.data.util.IndexedContainer._
import com.vaadin.ui.Table.ColumnGenerator
import com.vaadin.terminal.ThemeResource
import com.vaadin.ui._
import themes.BaseTheme
import com.vaadin.Application

/**
 * Module for the table to edit the keywords
 */
class KeywordsTableModule(configService: GDSConfigurationService) extends GDSWebModule {
  val title = "Keyword Configuration"
  val order = 0
  val dataSource = new GDSKeywordsDataSource(configService.getConfiguration)

  val tabLayout = new VerticalLayout
  val table = new Table("Keywords")

  var deleteIcon = new ThemeResource("../runo/icons/16/document-delete.png")
  var deleteTooltip = "Delete row"
  val deleteProperty = "DEL"

  def setupDeleteColumn(table: Table) {
    table.addGeneratedColumn(deleteProperty, new ColumnGenerator {
      def generateCell(source: Table, itemId: AnyRef, columnId: AnyRef) = {
        val deleteButton = new Button("")
        deleteButton.setStyleName(BaseTheme.BUTTON_LINK)
        deleteButton.setIcon(deleteIcon)
        deleteButton.setDescription(deleteTooltip)
        deleteButton.addListener((e: Button#ClickEvent) => {
          dataSource.removeItem(itemId)
          println("htoeo")
        })
        deleteButton
      }
    })
    table.setColumnIcon(deleteProperty, deleteIcon)
    table.setColumnHeader(deleteProperty, "")
    table.setColumnAlignment(deleteProperty, Table.ALIGN_CENTER)
  }

  def getDataSource(user: AnyRef) = Option(user) map {_ => dataSource} getOrElse {new ReadOnlyGDSKeywordsDataSource(configService.getConfiguration)}

  override def userChanged(user: AnyRef) = {
    table.setContainerDataSource(getDataSource(user))
    table.requestRepaintAll()
    tabLayout.replaceComponent(table, table)
  }

  override def buildTabContent(app: Application) = {
    table.setContainerDataSource(getDataSource(app.getUser))
    table.setNullSelectionAllowed(false)
    table.setImmediate(true)
    table.addStyleName("keywords-table")
    table.setSizeFull
    table.setPageLength(25)
    table.setCacheRate(0.2)
    //table.setEditable(true)
    table.setColumnCollapsingAllowed(true)
    table.setColumnReorderingAllowed(true)

    val user = app.getUser
    Option(user) foreach {
      _ => setupDeleteColumn(table)
    }

    // Center each column
    dataSource.getContainerPropertyIds foreach {
      table.setColumnAlignment(_, Table.ALIGN_LEFT)
    }

    tabLayout.addComponent(table)
    tabLayout.setExpandRatio(table, 1.0f)

    /*val pagingControls = table.createControls()
    pagingControls.setWidth("100%")
    pagingControls.setDebugId("pagingControls")
    // Trick to get th layouts right
    pagingControls.getComponent(0).setWidth("100%")
    layout.addComponent(pagingControls)*/
    tabLayout.addComponent(statusRow(app.getMainWindow))

    tabLayout.setSizeFull
    tabLayout
  }

  def statusRow(mainWindow: Window) = {
    val layout = new HorizontalLayout
    val button = buildValidateButton(mainWindow)
    val label = new Label("Total keywords: ")

    layout.setWidth("100%")
    layout.addStyleName("keywords-status")
    layout.addComponent(label)
    layout.addComponent(button)
    layout.setComponentAlignment(label, Alignment.MIDDLE_LEFT)
    layout.setExpandRatio(label, 1.0f)
    layout.setComponentAlignment(button, Alignment.MIDDLE_RIGHT)
    layout

    val panel = new Panel()
    panel.addComponent(layout)
    panel
  }

  def buildValidateButton(mainWindow: Window): Button = {
    val button: Button = new Button("Validate")

    button.addListener((e: Button#ClickEvent) => {
      mainWindow.showNotification("Validating...", Notification.TYPE_HUMANIZED_MESSAGE)
      println(dataSource.toGDSConfiguration.head)
    })
    button
  }
}