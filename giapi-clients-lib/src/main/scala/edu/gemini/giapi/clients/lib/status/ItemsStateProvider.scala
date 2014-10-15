package edu.gemini.giapi.clients.lib.status

import edu.gemini.jms.api.JmsProvider
import edu.gemini.aspen.giapi.util.jms.status.StatusGetter
import scala.collection.JavaConverters._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import edu.gemini.aspen.giapi.status.StatusItem

import scalaz._
import Scalaz._

class ItemsStateProvider {

  def retrieveCurrentState(provider: JmsProvider):Future[Iterable[StatusItem[_]]] = {
    val getter = new StatusGetter("GPI Engineering Tool Initial Loader")
    Future.apply {
      getter.startJms(provider)
      val result = ~Option(getter.getAllStatusItems).map(_.asScala.toList)
      getter.stopJms()
      result
    }
  }

}
