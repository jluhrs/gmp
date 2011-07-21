package edu.gemini.aspen.gds.web.ui.keywords

import edu.gemini.aspen.gds.web.ui.api.DefaultAuthenticationService
import com.vaadin.ui.LoginForm.LoginListener
import com.vaadin.ui.Window.Notification
import com.vaadin.ui._
import model._
import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.giapi.data.FitsKeyword

/**
 * Represents the LoginWindow
 */
class NewRowWindow(id: Int) extends Window("Add new row") {
  // Contains the factories for each column
  val columnsDefinitions = List(
    new InstrumentPropertyItemWrapperFactory,
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
  //setWidth("400px")
  //setHeight("195px")

  val layout = new FormLayout
  layout.setMargin(true)
  layout.setSizeUndefined()

  columnsDefinitions map {
    cd => {
      //cd.
    }
  }
  val index = new Label(id.toString)
  val instrument = new TextField("Instrument")
  instrument.setRequired(true)

  //val initialConfig = GDSConfiguration("", "", "", 0, "DOUBLE", "")

  val observationEvent = new GDSEventPropertyFactory()
  //observationEvent.setRequired(true)
  layout.addComponent(index)
  layout.addComponent(instrument)
  //layout.addComponent(observationEvent)
  setContent(layout)

}