package edu.gemini.aspen.gds.web.ui.vaadin

import com.vaadin.ui._
import edu.gemini.aspen.gds.web.ui.api.VaadinUtilities._
import edu.gemini.aspen.gds.web.ui.api.DefaultAuthenticationService
import com.vaadin.ui.LoginForm.LoginListener
import themes.BaseTheme
import com.vaadin.ui.Window.Notification

/**
 * Represents the LoginWindow
 */
class LoginWindow(parent: GDSCoreVaadinApp) extends Window("Authentication Required !") {
  setName("Login")
  setModal(true)
  setResizable(false)
  setWidth("365px")
  setHeight("195px")

  val layout = new VerticalLayout
  layout.setWidth("348px")
  layout.setHeight("186px")
  layout.setMargin(true)

  val loginForm = new LoginForm {
    override def getLoginHTML() = {
      val htmlBytes = super.getLoginHTML
      val htmlString =  new String(htmlBytes)
      htmlString.replace(
        "<input class='v-textfield' style='display:block;",
        "<input class='v-textfield' style='margin-bottom:10px; display:block;").getBytes
    }
  }

  loginForm.addListener(new LoginListener {
    def onLogin(event: LoginForm#LoginEvent) {
      close()
      if (authenticate(event.getLoginParameter("username"), event.getLoginParameter("password"))) {
        close()
      } else {
        parent.getMainWindow.showNotification("Authentication Failed!", Notification.TYPE_ERROR_MESSAGE)
      }
    }
  })
  loginForm.setWidth("350px")
  loginForm.setHeight("186px")
  layout.addComponent(loginForm)

  setContent(layout)

  // TODO replace by an actual service
  def authenticate(username: String, password: String) = {
    new DefaultAuthenticationService().authenticate(username, password)
  }

}