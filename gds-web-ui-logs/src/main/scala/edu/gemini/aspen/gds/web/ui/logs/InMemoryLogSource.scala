package edu.gemini.aspen.gds.web.ui.logs

import model.LogEventWrapper
import org.apache.felix.ipojo.annotations._
import org.ops4j.pax.logging.spi.{PaxLoggingEvent, PaxAppender}
import collection.mutable.ConcurrentMap
import scala.collection.JavaConversions._
import com.google.common.collect.MapMaker
import java.util.concurrent.TimeUnit._
import java.util.concurrent.atomic.AtomicInteger

/**
 * A PaxAppender service that will get log events from pax-logging */
@Component
@Instantiate
@Provides(specifications = Array(classOf[PaxAppender], classOf[LogSource]))
class InMemoryLogSource extends PaxAppender with LogSource {
  val MAXSIZE = 1000
  /** Service property that matches the configuration on the org.ops4j.pax.logging.cfg file */
  @ServiceProperty(name = "org.ops4j.pax.logging.appender.name", value = "GeminiAppender")
  val name = "GeminiAppender"

  // expiration of 1 day by default but tests can override it
  def expirationMillis = 24 * 60 * 60 * 1000

  // We index with an artificial value to avoid collisions with timestamps
  val index = new AtomicInteger(0)
  val logEventsMap: ConcurrentMap[Int, LogEventWrapper] = new MapMaker()
    .expireAfterWrite(expirationMillis, MILLISECONDS)
    .maximumSize(MAXSIZE)
    .makeMap[Int, LogEventWrapper]()

  @Validate
  def initLogListener() {
  }

  override def doAppend(event: PaxLoggingEvent) {
    val i = index.incrementAndGet()
    logEventsMap += i -> new LogEventWrapper(event)
  }

  override def logEvents = logEventsMap.values.toList.reverse
}