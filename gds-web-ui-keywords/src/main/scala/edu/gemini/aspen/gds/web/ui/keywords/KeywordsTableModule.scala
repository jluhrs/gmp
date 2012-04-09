package edu.gemini.aspen.gds.web.ui.keywords

import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import edu.gemini.aspen.gds.api.configuration.GDSConfigurationService
import edu.gemini.aspen.giapi.web.ui.vaadin.components._
import edu.gemini.aspen.giapi.web.ui.vaadin._
import model.{GDSKeywordsDataSource, WritableGDSKeywordsDataSource, ReadOnlyGDSKeywordsDataSource}
import com.vaadin.ui.Table.ColumnGenerator
import com.vaadin.terminal.ThemeResource
import com.vaadin.Application
import org.vaadin.dialogs.ConfirmDialog
import com.vaadin.ui.Window.Notification
import com.vaadin.ui.{Table, Alignment}
import edu.gemini.aspen.giapi.web.ui.vaadin.layouts.{HorizontalLayout, VerticalLayout}
import com.vaadin.ui.{Button => VaadinButton}
import org.vaadin.codeeditor.AceMarkerEditor
import org.vaadin.codeeditor.gwt.ace.AceMode
import org.vaadin.codeeditor.gwt.shared.Marker

/**
 * Module for the table to edit the keywords */
class KeywordsTableModule(configService: GDSConfigurationService) extends GDSWebModule {
  val title = "Keyword Configuration"
  val order = 2
  var dataSource: GDSKeywordsDataSource = buildDataSource(None)

  val tabLayout = new VerticalLayout(sizeFull = true)
  val table = new Table("Keywords")

  var deleteIcon = new ThemeResource("../runo/icons/16/document-delete.png")
  val deleteTooltip = "Delete row"
  val deleteProperty = "DEL"

  val saveButton = new Button(caption = "Save..", action = save, debugId = KeywordsTableModule.SAVE_BUTTON_DEBUG_ID)
  val newRowButton = new Button(caption = "New row...", action = newRow, debugId = KeywordsTableModule.NEW_ROW_BUTTON_DEBUG_ID)


  def visibleColumns(user: Option[String]): Array[AnyRef] = {
    val prop = user map {
      _ => deleteProperty
    } toList

    val cols = dataSource.propertyIds
    val p = cols ++ prop
    (p toArray).asInstanceOf[Array[AnyRef]]
  }

  protected[keywords] def buildDataSource(user: Option[String]): GDSKeywordsDataSource = user match {
    case Some(u) => new WritableGDSKeywordsDataSource(configService.getFullConfiguration)
    case None => new ReadOnlyGDSKeywordsDataSource(configService.getFullConfiguration)
  }

  private def updateTableColumns() = {
    dataSource.propertyIds foreach {
      p => table.setColumnHeader(p, dataSource.propertyHeader(p))
      table.setColumnWidth(p, dataSource.propertyWidth(p))
    }
    table.setColumnWidth(deleteProperty, 20)
  }

  override def userChanged(user: Option[String]) = {
    dataSource = buildDataSource(user)
    user match {
      case Some(u) => {
        setupDeleteColumn(table)
        updateTableColumns()
        // Update user dependant parts
      }
      case _ => {
        table.removeGeneratedColumn(deleteProperty)
        updateSaveButton(user)
        updateNewButton(user)
      }
    }
    updateSaveButton(user)
    updateNewButton(user)
    table.setContainerDataSource(dataSource)
    table.setVisibleColumns(visibleColumns(user))
    table.requestRepaintAll()
    tabLayout.replaceComponent(table, table)
  }

  private def updateNewButton(user: Option[String]) {
    newRowButton.setVisible(user.isDefined)
  }

  private def updateSaveButton(user: Option[String]) {
    saveButton.setVisible(user.isDefined)
  }

  def getAppUser(app: Application): Option[String] = app.getUser match {
    case Some(x: String) => Some[String](x)
    case _ => None
  }

  override def buildTabContent(app: Application) = {
    if (configService.hasError) {
      // Should produce error layout
      val code = new AceMarkerEditor
      code.setMode(AceMode.textile, "ace-gds.js")
      configService.errors foreach {
        case (msg, offset, position) => println(msg);code.addMarker(Marker.newErrorMarker(offset , offset + 2, msg))
      }
      code.setValue(configService.textContent)
      code.setWidth("100%")
      code.setHeight("100%")
      code.setReadOnly(true)
      tabLayout.add(code, ratio = 1f)
    } else {
      table.setContainerDataSource(dataSource)
      table.setNullSelectionAllowed(false)
      table.setImmediate(true)
      table.addStyleName("keywords-table")
      table.setSizeFull()
      table.setPageLength(25)
      table.setCacheRate(0.1)
      table.setColumnCollapsingAllowed(true)
      table.setColumnReorderingAllowed(true)

      tabLayout.add(table, ratio = 1f)

      tabLayout.add(statusRow(app))

      userChanged(getAppUser(app))
    }

    tabLayout
  }

  def statusRow(app: Application) = {
    val label = new Label("Keywords count: " + table.getContainerDataSource.size)
    val visible = getAppUser(app) match {
      case Some(x: String) => true
      case _ => false
    }
    newRowButton.setVisible(visible)
    saveButton.setVisible(visible)

    val layout = new HorizontalLayout(style = "keywords-control", margin = false, width = 100 percent) {
      add(label, ratio = 1f, alignment = Alignment.MIDDLE_LEFT)
      add(newRowButton, alignment = Alignment.MIDDLE_RIGHT)
      add(saveButton, alignment = Alignment.MIDDLE_RIGHT)
    }

    layout
  }

  override def refresh(app: Application) {
    if (configService.hasError) {
      app.getMainWindow.showNotification("Error in configuration", "<br/>There is an error in the configuration, please check the configuration and save!", Notification.TYPE_ERROR_MESSAGE)
    } else {
      val user = getAppUser(app)
      dataSource = buildDataSource(user)
      table.setContainerDataSource(dataSource)
      table.requestRepaintAll()
      tabLayout.replaceComponent(table, table)
    }
  }

  def setupDeleteColumn(table: Table) {
    table.addGeneratedColumn(deleteProperty, new ColumnGenerator {
      def generateCell(source: Table, itemId: AnyRef, columnId: AnyRef) = {
        def confirmDelete() = {
          val message = "Do you want to delete the item " + itemId + "?\n" +
            "Keyword: %s".format(dataSource.getItem(itemId).getItemProperty("FitsKeyword"))
          ConfirmDialog.show(table.getApplication.getMainWindow, "Please Confirm:", message,
            "Yes", "No", new ConfirmDialog.Listener() {

              def onClose(dialog: ConfirmDialog) {
                if (dialog.isConfirmed()) {
                  // Confirmed to continue
                  dataSource.removeItem(itemId)
                }
              }
            })
        }
        new LinkButton(caption = "", icon = deleteIcon, action = _ => confirmDelete, description = deleteTooltip)
      }
    })
    table.setColumnIcon(deleteProperty, deleteIcon)
    table.setColumnHeader(deleteProperty, deleteProperty)
    table.setColumnAlignment(deleteProperty, Table.ALIGN_CENTER)
    table.setColumnWidth(deleteProperty, 20)
  }

  private def newRow(e: VaadinButton#ClickEvent) {
    e.getComponent.getApplication.getMainWindow.addWindow(new NewRowWindow(dataSource))
  }

  private def save(e: VaadinButton#ClickEvent) = {
    configService.updateConfiguration(dataSource.toGDSConfiguration)
    e.getComponent.getApplication.getMainWindow.showNotification("Saving...", Notification.TYPE_HUMANIZED_MESSAGE)
  }

}

object KeywordsTableModule {
  val SAVE_BUTTON_DEBUG_ID = "gds-web-ui-keywords.status.save"
  val NEW_ROW_BUTTON_DEBUG_ID = "gds-web-ui-keywords.status.newrow"
}