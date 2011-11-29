package edu.gemini.aspen.gds.web.ui.api

/**
 * Interface for a service to authenticate users
 */
trait AuthenticationService {
    def authenticate(username:String, password:String):Boolean
}

/**
 * Default implementation of Authentication Service that returns always true */
class DefaultAuthenticationService extends AuthenticationService {
    def authenticate(username:String, password:String) = true
}