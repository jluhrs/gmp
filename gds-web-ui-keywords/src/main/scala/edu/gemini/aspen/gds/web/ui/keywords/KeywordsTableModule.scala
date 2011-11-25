package edu.gemini.aspen.gds.web.ui.keywords

import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import edu.gemini.aspen.gds.api.configuration.GDSConfigurationService
import edu.gemini.aspen.giapi.web.ui.vaadin._
import edu.gemini.aspen.giapi.web.ui.vaadin.components._
import model.{GDSKeywordsDataSource, WritableGDSKeywordsDataSource, ReadOnlyGDSKeywordsDataSource}
import scala.collection.JavaConversions._
import com.vaadin.ui.Table.ColumnGenerator
import com.vaadin.terminal.ThemeResource
import com.vaadin.Application
import org.vaadin.dialogs.ConfirmDialog
import com.vaadin.ui.Window.Notification
import com.vaadin.ui.{Button, Table, Alignment}
import com.vaadin.ui.{VerticalLayout, HorizontalLayout}
import com.vaadin.ui.themes.BaseTheme

/**
 * Module for the table to edit the keywords */
class KeywordsTableModule(configService: GDSConfigurationService) extends GDSWebModule {
  val title = "Keyword Configuration"
  val order = 2
  var dataSource: GDSKeywordsDataSource = buildDataSource(None)
  
  val tabLayout = new VerticalLayout
  val table = new Table("Keywords")

  var deleteIcon = new ThemeResource("../runo/icons/16/document-delete.png")
  val deleteTooltip = "Delete row"
  val deleteProperty = "DEL"

  val saveButton = new Button("Save..")
  val newRowButton = new Button("New row...")

  def visibleColumns(user: Option[String]): Array[AnyRef] = {
    val prop = user map {
        _ => deleteProperty
    } toList

    val cols = dataSource.propertyIds
    val p = cols ++ prop
    (p toArray).asInstanceOf[Array[AnyRef]]
  }

  protected[keywords] def buildDataSource(user: Option[String]): GDSKeywordsDataSource = {
    user match {
      case Some(u) => new WritableGDSKeywordsDataSource(configService.getFullConfiguration)
      case None => new ReadOnlyGDSKeywordsDataSource(configService.getFullConfiguration)
    }
  }


  private def updateTableColumns() = {
    dataSource.propertyIds foreach {
        p => table.setColumnHeader(p, dataSource.propertyHeader(p))
    }
    dataSource.propertyIds foreach {
        c => table.setColumnWidth(c, dataSource.propertyWidth(c))
    }
    table.setColumnWidth(deleteProperty, 20)
  }

  override def userChanged(user: Option[String]) = {
    dataSource = buildDataSource(user)
    table.setContainerDataSource(dataSource)
    user match {
      case Some(u) => {
        setupDeleteColumn(table)
        updateTableColumns()
        // Update user dependant parts
        updateSaveButton(user)
        updateNewButton(user)
      }
      case _ => {
        table.removeGeneratedColumn(deleteProperty)
      }
    }
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

  def getAppUser(app: Application): Option[String] = {
    app.getUser match {
      case Some(x: String) => Some[String](x)
      case _ => None
    }
  }

  override def buildTabContent(app: Application) = {
    table.setContainerDataSource(dataSource)
    table.setNullSelectionAllowed(false)
    table.setImmediate(true)
    table.addStyleName("keywords-table")
    table.setSizeFull()
    table.setPageLength(25)
    table.setCacheRate(0.1)
    table.setColumnCollapsingAllowed(true)
    table.setColumnReorderingAllowed(true)

    setupNewButton(table)

    tabLayout.addComponent(table)
    tabLayout.setExpandRatio(table, 1.0f)

    tabLayout.addComponent(statusRow(app))

    tabLayout.setSizeFull
    userChanged(getAppUser(app))

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
    layout.addComponent(newRowButton)
    layout.addComponent(saveButton)
    layout.setComponentAlignment(newRowButton, Alignment.MIDDLE_RIGHT)
    layout.setComponentAlignment(saveButton, Alignment.MIDDLE_RIGHT)

    layout
  }

  override def refresh(app: Application) {
    val user = getAppUser(app)
    dataSource = buildDataSource(user)
    table.setContainerDataSource(dataSource)
    table.requestRepaintAll()
    tabLayout.replaceComponent(table, table)
  }

  def setupDeleteColumn(table: Table) {
    table.addGeneratedColumn(deleteProperty, new ColumnGenerator {
      def generateCell(source: Table, itemId: AnyRef, columnId: AnyRef) = {
        val deleteButton = new Button("")
        deleteButton.setStyleName(BaseTheme.BUTTON_LINK)
        deleteButton.setIcon(deleteIcon)
        deleteButton.setDescription(deleteTooltip)
        deleteButton.addListener((e: Button#ClickEvent) => {
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
        })
        deleteButton
      }
    })
    table.setColumnIcon(deleteProperty, deleteIcon)
    table.setColumnHeader(deleteProperty, deleteProperty)
    table.setColumnAlignment(deleteProperty, Table.ALIGN_CENTER)
    table.setColumnWidth(deleteProperty, 20)
  }

  private def setupNewButton(table: Table) {
    newRowButton.addListener((e: Button#ClickEvent) => {
      table.getApplication.getMainWindow.addWindow(new NewRowWindow(dataSource))
    })
    newRowButton
  }

  private def buildSaveButton(app: Application): Button = {
    saveButton.addListener((e: Button#ClickEvent) => {
      configService.updateConfiguration(dataSource.toGDSConfiguration)
      app.getMainWindow.showNotification("Saving...", Notification.TYPE_HUMANIZED_MESSAGE)
      //todo: check that file hasn't changed between the time it was read and now.
    })
    saveButton
  }

}