package edu.gemini.aspen.gds.web.ui.modules

import edu.gemini.aspen.gds.web.ui.api.AuthenticationService
import org.osgi.service.useradmin.UserAdmin
import org.apache.felix.ipojo.annotations.{Requires, Provides, Instantiate, Component}

@Component
@Instantiate
@Provides
class UserAdminAuthenticationService(@Requires userAdmin: UserAdmin) extends AuthenticationService {
  def authenticate(username: String, password: String) = {
    val user = Option( userAdmin.getUser("username", username))
    println(user)
    val auth = user map {
      _.hasCredential("pwd", password)
    }
    println(auth)
    false
  }
}