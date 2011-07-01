package edu.gemini.aspen.gds.web.ui.keywords

import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import edu.gemini.aspen.gds.api.configuration.GDSConfigurationService
import org.apache.felix.ipojo.annotations.{Requires, Provides, Instantiate, Component}
import com.vaadin.ui.{Panel, Table}
import scala.Predef._
import edu.gemini.aspen.gds.api.{ArrayIndex, Mandatory, GDSConfiguration, Instrument}

@Component
@Instantiate
@Provides(specifications = Array(classOf[GDSWebModule]))
class KeywordsTableModule(@Requires configService: GDSConfigurationService) extends GDSWebModule {
    val title = "Keyword Configuration"
    val order = 0
    val columns = Map[Class[_], Any](classOf[Mandatory] -> classOf[Boolean], classOf[ArrayIndex] -> classOf[String])

    override def buildTabContent = {
        val table = new Table("")
        table.setSizeFull
        table.setSelectable(true)
        table.setColumnCollapsingAllowed(true)

        classOf[GDSConfiguration].getDeclaredFields foreach {
            c => table.addContainerProperty(c.getType.getSimpleName, "".getClass, "")
        }
        configService.getConfiguration foreach {
            v => table.addItem(configToItem(v), v)
        }
        table
    }

    def configToItem(config: GDSConfiguration) = {
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