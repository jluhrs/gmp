package edu.gemini.aspen.gds.web.ui.keywords

import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import edu.gemini.aspen.gds.api.configuration.GDSConfigurationService
import org.apache.felix.ipojo.annotations.{Requires, Provides, Instantiate, Component}
import scala.Predef._
import edu.gemini.aspen.gds.api.{Mandatory, GDSConfiguration}
import com.vaadin.ui._
import edu.gemini.aspen.gds.web.ui.api.VaadinUtilities._
import com.vaadin.ui.Window.Notification

abstract class ColumnDefinition(clazz: Class[_], val columnType:Class[_]) {
  def title = clazz.getSimpleName

  def buildComponent(config: GDSConfiguration):Any
}

class DefaultColumnDefinition(clazz: Class[_]) extends ColumnDefinition(clazz, classOf[String]) {
  def buildComponent(config: GDSConfiguration) = title
}

class MandatoryColumnDefinition() extends ColumnDefinition(classOf[Mandatory], classOf[CheckBox]) {
  def buildComponent(config: GDSConfiguration) = title
}

class KeywordsTableModule(@Requires configService: GDSConfigurationService) extends GDSWebModule {
  val title = "Keyword Configuration"
  val order = 0
  // Contains the non default column definitions
  val columnsDefinitions = Map[Class[_], ColumnDefinition](classOf[Mandatory] -> new MandatoryColumnDefinition())

  def defaultColumnDefinition(clazz: Class[_]) = new DefaultColumnDefinition(clazz)

  def buildValidateButton(mainWindow: Window): Button = {
    val button: Button = new Button("Validate")

    button.addListener(((e: Button#ClickEvent) => {
      mainWindow.showNotification("Validating...", Notification.TYPE_HUMANIZED_MESSAGE)
    }))
    button
  }

  override def buildTabContent(mainWindow:Window) = {
    val layout = new VerticalLayout

    val table = new Table("")
    table.setSelectable(true)
    table.setSizeFull
    table.setColumnCollapsingAllowed(true)

    classOf[GDSConfiguration].getDeclaredFields foreach {
      c => {
        val cd = columnsDefinitions.getOrElse(c.getType, defaultColumnDefinition(c.getType))
        table.addContainerProperty(cd.title, cd.columnType, "")
      }
    }
    configService.getConfiguration foreach {
      v => table.addItem(configToItem(v), v)
    }
    
    layout.addComponent(table)
    layout.setExpandRatio(table, 1.0f)
    
    val button = buildValidateButton(mainWindow)

    layout.addComponent(button)
    layout.setComponentAlignment(button, Alignment.MIDDLE_RIGHT)
    layout.setSizeFull
    layout
  }

  def configToItem(config: GDSConfiguration):Array[Object] = {
    /*for {f <- classOf[GDSConfiguration].getDeclaredFields
        val cd = columnsDefinitions.getOrElse(f.getType, defaultColumnDefinition(f.getType))
        table.addContainerProperty(cd.title, cd.columnType, "")
    } yield cd.buildComponent(config)*/
    Array[Object](config.instrument.name.toString,
      config.event.name.toString,
      config.keyword.getName.toString,
      config.index.index.toString,
      config.dataType.name.toString,
      config.mandatory.mandatory.toString,
      config.nullValue.value.toString,
      config.subsystem.name.toString,
      config.channel.name.toString,
      config.arrayIndex.value.toString,
      config.fitsComment.value.toString
    )
  }
}