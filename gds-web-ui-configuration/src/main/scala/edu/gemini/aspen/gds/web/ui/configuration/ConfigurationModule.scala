package edu.gemini.aspen.gds.web.ui.configuration

import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import com.vaadin.Application
import com.vaadin.data.util.ObjectProperty
import edu.gemini.aspen.gmp.services.PropertyHolder
import org.osgi.service.cm.ConfigurationAdmin
import java.util.{Hashtable, Dictionary}
import org.osgi.service.cm.Configuration
import edu.gemini.aspen.gds.web.ui.api.Preamble._
import com.vaadin.ui.{TextField, GridLayout, Button, Label, FormLayout, Panel, Component}
import edu.gemini.aspen.gmp.services.properties.GmpProperties


class ConfigurationModule(propHolder: PropertyHolder, configAdmin: ConfigurationAdmin) extends GDSWebModule {
  val title: String = "System Configuration"
  val order: Int = 5


  private val _properties = createProperties(GmpProperties.values().toList)

  private def createProperties(props: List[GmpProperties]): List[Property] = {
    if (props.isEmpty) {
      Nil
    } else {
      new Property(props.head) :: createProperties(props.tail)
    }
  }

  private class Property(val prop: GmpProperties) {
    val textField = new TextField()
    val objectProperty = new ObjectProperty(prop.getDefault)
    textField.setPropertyDataSource(objectProperty)
    val label = new Label("<b>" + prop.name() + "</b>", Label.CONTENT_XHTML)
  }

  override def buildTabContent(app: Application): Component = {
    val layout = new GridLayout(3, GmpProperties.values().size + 1)
    layout.setMargin(true)
    layout.setSpacing(true)

    for (property <- _properties) {
      layout.addComponent(property.label)
      layout.addComponent(property.textField)
      val button = new Button("Save")
      button.addListener((e: Button#ClickEvent) => {
        setProperty(property.prop.name(), property.textField.getValue.toString)
      })
      layout.addComponent(button)
    }
    val button = new Button("Reload")
    button.addListener((e: Button#ClickEvent) => {
      refresh()
    })
    layout.addComponent(button, 2, GmpProperties.values().size)
    layout
  }

  override def refresh() {
    for (property <- _properties) {
      property.objectProperty.setValue(getProperty(property.prop.name()))
    }
  }

  private def getProperty(propName: String) = {
    propHolder.getProperty(propName)
  }

  private def setProperty(propName: String, propValue: String) {
    var config: Configuration = configAdmin.listConfigurations("(service.factorypid=edu.gemini.aspen.gmp.services.properties.SimplePropertyHolder)")(0)

    var props: Dictionary[String, String] = config.getProperties.asInstanceOf[Dictionary[String, String]]

    if (props == null) {
      props = new Hashtable[String, String]()
    }

    props.put(propName, propValue)
    config.update(props)
  }
}


object ConfigurationModule {

}