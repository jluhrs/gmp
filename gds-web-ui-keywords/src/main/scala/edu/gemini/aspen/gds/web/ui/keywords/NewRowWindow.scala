package edu.gemini.aspen.gds.web.ui.keywords

import edu.gemini.aspen.giapi.web.ui.vaadin._
import edu.gemini.aspen.giapi.web.ui.vaadin.components._
import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.gds.api.Conversions._
import model._
import com.vaadin.ui.{HorizontalLayout, FormLayout, Window}

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
  setWidth(400.px)

  val layout = new FormLayout
  layout.setMargin(true)
  layout.setSizeUndefined()
  val index = new Label((dataSource.last).toString, caption = "Configuration ID")

  layout.addComponent(index)
  val initialConfig = GDSConfiguration("GPI", "OBS_START_ACQ", "KEYWORD", 0, "DOUBLE", false, "NONE", "EPICS", "NONE", 0, "", "")
  val controlsAndWrappers = columnsDefinitions map {
    cd => cd.buildPropertyControlAndWrapper(initialConfig)
  }
  controlsAndWrappers map {
    case (c, _) => {
      c.setSizeUndefined()
      layout.addComponent(c)
    }
  }

  val okButton = new Button(caption = "Ok", action = _ => addNewConfig)
  val cancelButton = new Button(caption = "Cancel", action = _ => closeDialog)

  val buttonLayout = new HorizontalLayout
  buttonLayout.addComponent(okButton)
  buttonLayout.addComponent(cancelButton)

  layout.addComponent(buttonLayout)

  setContent(layout)

  /**
   * Adds a new configuration item from the contents of the dialog box */
  private def addNewConfig() {
    val newConfig = GDSKeywordsDataSource.itemToGDSConfiguration(initialConfig, controlsAndWrappers map {
      _._2
    })
    println("NEW " + newConfig)
    dataSource.addNewConfig(newConfig)
    closeDialog()
  }

  /**
   * Closes the dialog box */
  private def closeDialog() {close()}

}