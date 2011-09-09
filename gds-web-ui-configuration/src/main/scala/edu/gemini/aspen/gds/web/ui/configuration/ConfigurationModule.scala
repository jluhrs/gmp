package edu.gemini.aspen.gds.web.ui.configuration

import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import com.vaadin.Application
import com.vaadin.data.util.ObjectProperty
import edu.gemini.aspen.gmp.services.PropertyHolder
import org.osgi.service.cm.ConfigurationAdmin
import java.util.{Hashtable, Dictionary}
import org.osgi.service.cm.Configuration
import edu.gemini.aspen.gds.web.ui.api.Preamble._
import edu.gemini.aspen.gmp.services.properties.GmpProperties
import com.vaadin.ui.{GridLayout, Alignment, TextField, Button, Label, FormLayout, Panel, Component}

class ConfigurationModule(propHolder: PropertyHolder, configAdmin: ConfigurationAdmin) extends GDSWebModule {
  val title: String = "System Configuration"
  val order: Int = 3

  val _properties = createProperties(GmpProperties.values().toList)

  private def createProperties(props: List[GmpProperties]): List[Property] = {
    if (props.isEmpty) {
      Nil
    } else {
      new Property(props.head) :: createProperties(props.tail)
    }
  }

  class Property(val prop: GmpProperties) {
    val textField = new TextField()
    val objectProperty = new ObjectProperty(prop.getDefault)
    textField.setPropertyDataSource(objectProperty)
    val label = new Label("<b>" + prop.name() + "</b>", Label.CONTENT_XHTML)
  }

  override def buildTabContent(app: Application): Component = {
    val layout = new GridLayout(2, GmpProperties.values().size + 1)
    layout.setMargin(true)
    layout.setSpacing(true)

    for (property <- _properties) {
      layout.addComponent(property.label)
      layout.addComponent(property.textField)
    }

    val buttonLayout = new GridLayout(2, 1)
    buttonLayout.setSpacing(true)

    val saveButton = new Button("Save")
    saveButton.addListener((e: Button#ClickEvent) => {
      save()
    })
    buttonLayout.addComponent(saveButton)

    val reloadButton = new Button("Reload")
    reloadButton.addListener((e: Button#ClickEvent) => {
      refresh(app)
    })
    buttonLayout.addComponent(reloadButton)

    layout.addComponent(buttonLayout, 1, GmpProperties.values().size)
    layout
  }

  override def refresh(app: Application) {
    for (property <- _properties) {
      property.objectProperty.setValue(getProperty(property.prop.name()))
    }
  }

  private def getProperty(propName: String) = {
    propHolder.getProperty(propName)
  }

  private def save() {
    var config: Configuration = configAdmin.listConfigurations("(service.factorypid=edu.gemini.aspen.gmp.services.properties.SimplePropertyHolder)")(0)

    var props: Dictionary[String, String] = config.getProperties.asInstanceOf[Dictionary[String, String]]

    if (props == null) {
      props = new Hashtable[String, String]()
    }
    for (property <- _properties) {
      props.put(property.prop.name(), property.textField.getValue.toString)
    }
    config.update(props)

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