package edu.gemini.aspen.gds.web.ui.vaadin

import com.vaadin.ui._
import edu.gemini.aspen.gds.web.ui.api.Preamble._
import com.vaadin.ui.Window.Notification
import edu.gemini.aspen.gds.web.ui.api.AuthenticationService

/**
 * Represents the LoginWindow
 */
class LoginWindow(parent: GDSCoreVaadinApp, authenticationService: AuthenticationService) extends Window("Authentication Required !") {
  setName("Login")
  setModal(true)
  setResizable(false)
  setWidth("365px")
  setHeight("210px")

  val layout = new VerticalLayout
  layout.setWidth("348px")
  layout.setHeight("168px")
  layout.setMargin(true)

  val loginForm = new LoginForm {
    override def getLoginHTML() = {
      val htmlBytes = super.getLoginHTML
      val htmlString = new String(htmlBytes)
      // Needs to do ugly replacement to be compatible with Password Managers
      htmlString.replace(
        "<input class='v-textfield' style='display:block;",
        "<input class='v-textfield' style='margin-bottom:10px; display:block;").getBytes
    }
  }

  loginForm.addListener((event: LoginForm#LoginEvent) => {
    close()
    if (authenticate(event.getLoginParameter("username"), event.getLoginParameter("password"))) {
      close()
      parent.authenticated(Option(event.getLoginParameter("username")))
    } else {
      parent.getMainWindow.showNotification("Authentication Failed!", Notification.TYPE_ERROR_MESSAGE)
    }
  })
  loginForm.setWidth("350px")
  loginForm.setHeight("180px")
  layout.addComponent(loginForm)

  setContent(layout)

  def authenticate(username: String, password: String) = {
    authenticationService.authenticate(username, password)
  }

}