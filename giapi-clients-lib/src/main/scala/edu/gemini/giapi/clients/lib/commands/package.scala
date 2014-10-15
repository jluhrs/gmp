package edu.gemini.giapi.clients.lib

import java.util.logging.Logger

import edu.gemini.aspen.giapi.commands.HandlerResponse.Response
import scala.concurrent.{Promise, Future}
import edu.gemini.aspen.giapi.commands.{HandlerResponse, CompletionListener, Command}
import edu.gemini.aspen.gmp.commands.jms.client.CommandSenderClient
import scala.concurrent.ExecutionContext.Implicits.global

package object commands {
  private val LOG = Logger.getLogger("edu.gemini.giapi.clients.lib.commands")

  sealed trait CommandResult {
    def isError:Boolean = false
  }
  case class Completed(response: Response) extends CommandResult
  case class Error(response: Response, message: String) extends CommandResult {
    override def isError = true
  }
  case class Accepted(commandName: Option[String], response: Response, completion: Future[CommandResult]) extends CommandResult
  case class CommandFailure(e: Throwable) extends CommandResult

  type CommandOperationResult = Future[CommandResult]

  def operationResult(commandsClient: CommandSenderClient, command: Command, commandName: Option[String] = None): CommandOperationResult = {
    val result             = Promise[CommandResult]()
    val acceptedCompletion = Promise[CommandResult]()
    Future.apply {
      LOG.info(s"About to send command $command")
      val hr = commandsClient.sendCommand(command, new CompletionListener {
        override def onHandlerResponse(hr: HandlerResponse, command: Command) = {
          if (hr.getResponse == Response.ERROR || hr.getResponse == Response.NOANSWER) {
            LOG.info(s"Response to command $command: ${hr.getResponse}")
            acceptedCompletion.success(Error(hr.getResponse, hr.getMessage))
          } else {
            acceptedCompletion.success(Completed(hr.getResponse))
          }
        }
      }, 2000)
      if (hr.getResponse == Response.ERROR || hr.getResponse == Response.NOANSWER) {
        LOG.info(s"No answer response to command $command: ${hr.getResponse}")
        result.success(Error(hr.getResponse, if (hr.getResponse == Response.NOANSWER) "No answer from the instrument" else hr.getMessage))
      } else if (hr.getResponse == Response.COMPLETED || hr.getResponse == Response.ACCEPTED) {
        LOG.info(s"Command completed immediately $command: ${hr.getResponse}")
        result.success(Completed(hr.getResponse))
      } else {
        LOG.info(s"Command accepted $command")
        result.success(Accepted(commandName, hr.getResponse, acceptedCompletion.future))
      }
    }
    result.future
  }
}
