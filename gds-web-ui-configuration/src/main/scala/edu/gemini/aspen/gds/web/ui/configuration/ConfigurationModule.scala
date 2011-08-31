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

class ConfigurationModule(propHolder: PropertyHolder, configAdmin: ConfigurationAdmin) extends GDSWebModule {
  val title: String = "System Configuration"
  val order: Int = 5

  import ConfigurationModule._

  val gmp_host_name = new TextField()
  val gmp_host_nameProp = new ObjectProperty(defaultGmp_host_name)
  gmp_host_name.setPropertyDataSource(gmp_host_nameProp)

  override def buildTabContent(app: Application): Component = {
    val layout = new GridLayout(3, 1)

    layout.addComponent(new Label("GMP_HOST_NAME"))
    layout.addComponent(gmp_host_name)
    val okButton = new Button("Apply")
    okButton.addListener((e: Button#ClickEvent) => {
      setProperty("GMP_HOST_NAME", gmp_host_name.getValue.toString)
    })


    layout.addComponent(okButton)
    layout
  }

  override def refresh() {
    gmp_host_nameProp.setValue(getProperty("GMP_HOST_NAME"))
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
  val defaultGmp_host_name = "INVALID"
}