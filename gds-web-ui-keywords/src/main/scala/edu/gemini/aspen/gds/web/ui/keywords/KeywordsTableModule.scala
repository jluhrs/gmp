package edu.gemini.aspen.gds.web.ui.keywords

import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import edu.gemini.aspen.gds.api.configuration.GDSConfigurationService
import org.apache.felix.ipojo.annotations.{Requires, Provides, Instantiate, Component}
import scala.Predef._
import edu.gemini.aspen.gds.api.{Mandatory, GDSConfiguration}
import com.vaadin.ui.{Window, CheckBox, Table}

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

@Component
@Instantiate
@Provides(specifications = Array(classOf[GDSWebModule]))
class KeywordsTableModule(@Requires configService: GDSConfigurationService) extends GDSWebModule {
  val title = "Keyword Configuration"
  val order = 0
  // Contains the non default column definitions
  val columnsDefinitions = Map[Class[_], ColumnDefinition](classOf[Mandatory] -> new MandatoryColumnDefinition())

  def defaultColumnDefinition(clazz: Class[_]) = new DefaultColumnDefinition(clazz)

  override def buildTabContent(mainWindow:Window) = {
    val table = new Table("")
    table.setSizeFull
    table.setSelectable(true)
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
    table
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