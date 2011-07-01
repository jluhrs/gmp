package edu.gemini.aspen.gds.web.ui.vaadin

import com.vaadin.ui._
import edu.gemini.aspen.gds.web.ui.vaadin.VaadinUtilities._
import edu.gemini.aspen.gds.web.ui.api.DefaultAuthenticationService
import com.vaadin.terminal.UserError

class LoginWindow(parent: GDSCoreVaadinApp) extends Window("Authentication Required !") {
    val loginButton = new Button("Login")
    val usernameField = new TextField("Username")
    val passwordField = new PasswordField("Password")

    loginButton.addListener((e: Button#ClickEvent) => tryLogin)

    setName("Login")
    setModal(true)
    setResizable(false)
    setWidth("200px")

    usernameField.setRequired(true)
    passwordField.setRequired(true)

    addComponent(new Label("Please login in order to edit configurations"))
    addComponent(new Label)
    addComponent(usernameField)
    addComponent(passwordField)
    addComponent(loginButton)

    def fieldValue(field: Field): Option[String] = {
        field.getValue match {
            case s: String if !s.isEmpty => Option[String](field.getValue.toString)
            case _ => None
        }
    }

    def tryLogin {
        val authPair = List(usernameField, passwordField) map {
            f => f -> fieldValue(f)
        } map {
            case (field, value) => value.orElse {
                showNotification(field.getCaption + " cannot be empty", Window.Notification.TYPE_WARNING_MESSAGE)
                field.setComponentError(new UserError(field.getCaption + " cannot be empty"))
                None
            }
        }
        authPair match {
            case Some(username) :: Some(password) :: Nil => {
                if (authenticate(username, password)) {
                    parent.authenticated(username)
                   close()
                }
            }
            case _ => 
        }
    }

    // TODO replace by an actual service
    def authenticate(username: String, password: String) = {
        new DefaultAuthenticationService().authenticate(username, password)
    }

}