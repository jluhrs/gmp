package edu.gemini.aspen.gds.web.ui.api

trait AuthenticationService {
    def authenticate(username:String, password:String):Boolean
}

class DefaultAuthenticationService extends AuthenticationService {
    def authenticate(username:String, password:String) = true
}