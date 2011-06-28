package edu.gemini.aspen.gds.webui

import javax.ws.rs.core.Response
import javax.ws.rs.{Produces, GET, Path}

@Path("channel")
class RestEnd {
    @GET
    @Produces(Array("application/json"))
    def doGet() = {
        Response.ok("some").build
    }
}