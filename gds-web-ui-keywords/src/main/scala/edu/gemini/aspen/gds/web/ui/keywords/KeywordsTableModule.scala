package edu.gemini.aspen.gds.web.ui.keywords

import _root_.edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import _root_.edu.gemini.aspen.gds.api.configuration.GDSConfigurationService
import _root_.edu.gemini.aspen.gds.web.ui.api.VaadinUtilities._
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

  val saveButton = new Button("Save..")

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

  def visibleColumns(user: AnyRef):Array[AnyRef] = {
    val prop:List[_] = Option(user) map {
      _ => deleteProperty
    } toList
    val cols:List[_] = getDataSource(user).getContainerPropertyIds toList
    val p:List[_] = cols ++ prop
    (p toArray).asInstanceOf[Array[AnyRef]]
  }

  def getDataSource(user: AnyRef) = Option(user) map {
    _ => dataSource
  } getOrElse {
    new ReadOnlyGDSKeywordsDataSource(configService.getConfiguration)
  }

  override def userChanged(user: AnyRef) = {
    table.setContainerDataSource(getDataSource(user))
    // Update user dependant parts
    updateSaveButton(user)
    println(visibleColumns(user))
    table.setVisibleColumns(visibleColumns(user))
    table.requestRepaintAll()
    tabLayout.replaceComponent(table, table)
  }

  def updateSaveButton(user: AnyRef) {
    saveButton.setVisible(Option(user).isDefined)
  }

  override def buildTabContent(app: Application) = {
    table.setContainerDataSource(getDataSource(app.getUser))
    table.setNullSelectionAllowed(false)
    table.setImmediate(true)
    table.addStyleName("keywords-table")
    table.setSizeFull()
    table.setPageLength(25)
    table.setCacheRate(0.1)
    //table.setEditable(true)
    table.setColumnCollapsingAllowed(true)
    table.setColumnReorderingAllowed(true)

    setupDeleteColumn(table)

    tabLayout.addComponent(table)
    tabLayout.setExpandRatio(table, 1.0f)

    /*val pagingControls = table.createControls()
    pagingControls.setWidth("100%")
    pagingControls.setDebugId("pagingControls")
    // Trick to get th layouts right
    pagingControls.getComponent(0).setWidth("100%")
    layout.addComponent(pagingControls)*/
    tabLayout.addComponent(statusRow(app))
    updateSaveButton(app.getUser)
    println(visibleColumns(app.getUser))
    table.setVisibleColumns(visibleColumns(app.getUser))

    tabLayout.setSizeFull
    tabLayout
  }

  def statusRow(app: Application) = {
    val layout = new HorizontalLayout
    layout.addStyleName("keywords-control")
    layout.setMargin(false)
    layout.setWidth("100%")
    layout.addStyleName("keywords-control")
    val label = new Label("Keywords count: " + table.getContainerDataSource.size)

    layout.addComponent(label)
    layout.setComponentAlignment(label, Alignment.MIDDLE_LEFT)
    layout.setExpandRatio(label, 1.0f)

    val saveButton = buildSaveButton(app)
    layout.addComponent(saveButton)
    layout.setComponentAlignment(saveButton, Alignment.MIDDLE_RIGHT)

//    val showAsTextButton = buildShowAsTextButton(app)
//    layout.addComponent(showAsTextButton)
//    layout.setComponentAlignment(showAsTextButton, Alignment.MIDDLE_RIGHT)

    layout
  }

  private def buildSaveButton(app: Application): Button = {
    saveButton.addListener((e: Button#ClickEvent) => {
      app.getMainWindow.showNotification("Validating...", Notification.TYPE_HUMANIZED_MESSAGE)
    })
    saveButton
  }

  private def buildShowAsTextButton(app: Application): Button = {
    val showAsTextButton = new Button("Show as text...")
    showAsTextButton.addListener((e: Button#ClickEvent) => {
      app.getMainWindow.showNotification("Display... " + configService, Notification.TYPE_HUMANIZED_MESSAGE)
    })
    showAsTextButton
  }
}