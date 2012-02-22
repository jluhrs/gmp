package edu.gemini.aspen.gds.web.ui.configuration

import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import com.vaadin.Application
import com.vaadin.data.util.ObjectProperty
import edu.gemini.aspen.gmp.services.PropertyHolder
import org.osgi.service.cm.ConfigurationAdmin
import java.util.{Hashtable, Dictionary}
import org.osgi.service.cm.Configuration
import edu.gemini.aspen.giapi.web.ui.vaadin._
import edu.gemini.aspen.giapi.web.ui.vaadin.components._
import edu.gemini.aspen.gmp.services.properties.GmpProperties
import annotation.tailrec
import com.vaadin.ui.{GridLayout, TextField, Component}

class ConfigurationModule(propHolder: PropertyHolder, configAdmin: ConfigurationAdmin) extends GDSWebModule {
  val title: String = "System Configuration"
  val order: Int = 3

  val _properties = createProperties(GmpProperties.values().toList)

  // TODO make tailrec
  // @tailrec
  private def createProperties(props: List[GmpProperties]): List[Property] = {
    if (props.isEmpty) {
      Nil
    } else {
      new Property(props.head) :: createProperties(props.tail)
    }
  }

  def getAppUser(app: Application): Option[String] = {
    app.getUser match {
      case Some(x: String) => Some[String](x)
      case _ => None
    }
  }

  class Property(val prop: GmpProperties) {
    val textField = new TextField()
    val objectProperty = new ObjectProperty(prop.getDefault)
    textField.setPropertyDataSource(objectProperty)
    val label = new Label(content=prop.name(), style="gds-bold")
  }

  override def buildTabContent(app: Application): Component = {
    val layout = new GridLayout(2, GmpProperties.values().size + 1)
    layout.setMargin(true)
    layout.setSpacing(true)

    for (property <- _properties) {
      layout.addComponent(property.label)
      layout.addComponent(property.textField)
    }

    setWritePermissions(app)

    val buttonLayout = new GridLayout(2, 1)
    buttonLayout.setSpacing(true)

    val saveButton = new Button(caption="Save", action = _ => {
      save()
    })

    val reloadButton = new Button(caption="Reload", action = _ => {
      refresh(app)
    })

    buttonLayout.addComponent(saveButton)
    buttonLayout.addComponent(reloadButton)

    layout.addComponent(buttonLayout, 1, GmpProperties.values.size)
    layout
  }

  /**
   * Depending on the user permissions this method will enable/disable the text fields */
  private def setWritePermissions(app:Application) {
    _properties foreach {
      _.textField.setEnabled(getAppUser(app).isDefined)
    }
  }

  override def refresh(app: Application) {
    for (property <- _properties) {
      property.objectProperty.setValue(getProperty(property.prop.name()))
    }
    setWritePermissions(app)
  }

  private def getProperty(propName: String) = propHolder.getProperty(propName)

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