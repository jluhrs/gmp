package edu.gemini.giapi.clients.lib.commands

import edu.gemini.aspen.giapi.commands._
import edu.gemini.aspen.gmp.commands.jms.client.CommandSenderClient
import org.junit.runner.RunWith
import org.junit.Assert._
import org.scalatest.FunSuite
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import org.mockito.Matchers._

@RunWith(classOf[JUnitRunner])
class CommandsTest extends FunSuite with MockitoSugar with ScalaFutures {
  test("no reply") {
    val sender = mock[CommandSenderClient]
    val command = new Command(SequenceCommand.TEST, Activity.PRESET)
    when(sender.sendCommand(anyObject(), anyObject(), anyInt())).thenReturn(HandlerResponse.NOANSWER)

    val r = operationResult(sender, command, None)

    whenReady(r) { x =>
      assertTrue(x.isError)
    }
  }
}
