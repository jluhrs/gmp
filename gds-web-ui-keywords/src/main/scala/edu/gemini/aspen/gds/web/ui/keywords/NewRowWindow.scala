package edu.gemini.aspen.gds.web.ui.keywords

import _root_.edu.gemini.aspen.gds.web.ui.api.Preamble._
import edu.gemini.aspen.gds.web.ui.api.DefaultAuthenticationService
import com.vaadin.ui.LoginForm.LoginListener
import com.vaadin.ui.Window.Notification
import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.gds.api.Conversions._
import edu.gemini.aspen.giapi.data.FitsKeyword
import model._
import com.vaadin.ui._

/**
 * Represents the LoginWindow
 */
class NewRowWindow(dataSource: GDSKeywordsDataSource) extends Window("Add new row") {
  // Contains the factories for each column
  val columnsDefinitions = List(
    new InstrumentPropertyFactory,
    new GDSEventPropertyFactory,
    new FitsKeywordPropertyFactory,
    new HeaderIndexPropertyFactory,
    new DataTypePropertyFactory,
    new MandatoryPropertyFactory,
    new DefaultValuePropertyFactory,
    new SubsystemPropertyFactory,
    new ChannelPropertyFactory,
    new ArrayIndexPropertyFactory,
    new FitsCommentPropertyFactory
  )

  setName("Add new row")
  setModal(true)
  setResizable(false)
  setWidth("400px")
  //setHeight("195px")

  val layout = new FormLayout
  layout.setMargin(true)
  layout.setSizeUndefined()
  val index = new Label((dataSource.last).toString)
  index.setCaption("Configuration ID")

  layout.addComponent(index)
  val initialConfig = GDSConfiguration("GPI", "OBS_START_ACQ", "KEYWORD", 0, "DOUBLE", true, "NONE", "EPICS", "NONE", 0, "")
  val controlsAndWrappers = columnsDefinitions map {
    cd => cd.buildPropertyControlAndWrapper(initialConfig)
  }
  controlsAndWrappers map {
    case (c, _) => {
      layout.addComponent(c)
    }
  }

  val buttonLayout = new HorizontalLayout

  val okButton = new Button("Ok")
  okButton.addListener((e: Button#ClickEvent) => {
    val newConfig = GDSKeywordsDataSource.itemToGDSConfiguration(initialConfig, controlsAndWrappers map {
      _._2
    })
    dataSource.addNewConfig(newConfig)
    close()
  })
  buttonLayout.addComponent(okButton)
  val cancelButton = new Button("Cancel")
  cancelButton.addListener((e: Button#ClickEvent) => {
    close()
  })
  buttonLayout.addComponent(cancelButton)

  layout.addComponent(buttonLayout)

  setContent(layout)

}