
package edu.gemini.gmp.status.translator

import edu.gemini.aspen.giapi.status.Health
import edu.gemini.aspen.giapi.status.StatusItem
import edu.gemini.aspen.giapi.status.impl.BasicStatus
import edu.gemini.aspen.giapi.status.impl.HealthStatus
import edu.gemini.aspen.giapi.util.jms.status.StatusGetter
import edu.gemini.gmp.top.Top
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.atomic.AtomicBoolean
import java.util.logging.Logger
import edu.gemini.gmp.status.translator.generated.{StatusType, DataType}
import java.io.{FileInputStream, File}
import scala.collection._

case class ItemTranslation[A, +B](destinationName: String, default: Option[B], translations: Map[A, B], destinationType: DataType) {
  def translate(value: A):StatusItem[_] = {
    val tr:TranslationType[_] = TranslationType.defaultTranslationType(destinationType)
    translateInternal(value)(tr)
  }
  private def translateInternal(value: A)(translation: TranslationType[_]):StatusItem[_] = {
    val d = default.getOrElse(translation.defaultValue)
    val defaultStatus = translation.toStatusItem(destinationName)(d.toString)
    translations.get(value).map(v => translation.toStatusItem(destinationName)(translation.toValue(v.toString).toString)).getOrElse(defaultStatus)
  }
}

trait TranslationType[A] {
  def toValue(value: String): A
  def toStatusItem(name: String)(value: String):StatusItem[A]
  def defaultValue: A
}

object TranslationType {
  implicit object IntTranslationType extends TranslationType[Int] {
    override def toValue(value: String):Int = value.toInt
    override def toStatusItem(name: String)(value: String) = new BasicStatus[Int](name, toValue(value))
    override val defaultValue = 0
  }
  implicit object DoubleTranslationType extends TranslationType[Double] {
    override def toValue(value: String):Double = value.toDouble
    override def toStatusItem(name: String)(value: String) = new BasicStatus[Double](name, toValue(value))
    override val defaultValue = 0.0
  }
  implicit object HealthTranslationType extends TranslationType[Health] {
    override def toValue(value: String):Health = Health.valueOf(value)
    override def toStatusItem(name: String)(value: String) = new HealthStatus(name, toValue(value))
    override val defaultValue = Health.BAD
  }
  implicit object StringTranslationType extends TranslationType[String] {
    override def toValue(value: String):String = value
    override def toStatusItem(name: String)(value: String) = new BasicStatus[String](name, toValue(value))
    override val defaultValue = ""
  }

  def toValue[A: TranslationType](value: String): A = implicitly[TranslationType[A]].toValue(value)

  def defaultTranslationType(statusType: DataType):TranslationType[_] = statusType match {
    case DataType.INT    => IntTranslationType
    case DataType.DOUBLE => DoubleTranslationType
    case DataType.HEALTH => HealthTranslationType
    case DataType.STRING => StringTranslationType
  }
}


abstract class AbstractStatusItemTranslator(top: Top, xmlFileName: String) extends StatusItemTranslator {
  private final val LOG: Logger = Logger.getLogger(classOf[AbstractStatusItemTranslator].getName)
  private final val CONF_DIR_PROPERTY: String = "statusTranslatorFile"

  val name = s"StatusItemTranslator: $this"
  var config: StatusItemTranslatorConfiguration = null
  val getter: StatusGetter = new StatusGetter("Status Translator initial item loader")
  val jmsStarted: AtomicBoolean = new AtomicBoolean(false)

  val fileExistence = for {
    x <- Option(new File(xmlFileName))
    if x.exists()
  } yield new StatusItemTranslatorConfiguration(new FileInputStream(x))

  def translationMap(config: StatusType) = {
    import scala.collection.JavaConverters._
    import TranslationType._

    val fromTranslator = defaultTranslationType(config.getOriginalType)
    val toTranslator = defaultTranslationType(config.getTranslatedType)

    val result = for {
      item <- config.getMaps.getMap.asScala
    } yield toValue(item.getFrom)(fromTranslator) -> toValue(item.getTo)(toTranslator)
    result.toMap
  }

  def extract(config: StatusItemTranslatorConfiguration): List[(String, ItemTranslation[Any, Any])] =
    for {
      status      <- config.statuses
      ot           = status.getOriginalType
      dt           = status.getTranslatedType
      i            = TranslationType.defaultTranslationType(dt)
      default      = Option(status.getDefault).map(TranslationType.toValue(_)(i))
      translations = translationMap(status)
    } yield top.buildStatusItemName(status.getOriginalName) -> ItemTranslation(top.buildStatusItemName(status.getTranslatedName), default, translations, dt)

  val translations = fileExistence.map(extract).map(_.groupBy(a => a._1)).getOrElse(Map.empty)  //val translations = fileExistence.map(extract).map(_.toMap).getOrElse(Map.empty)

  /**
   * Try to fetch items from the StatusDB at startup. Translate those found.
   */
  protected def initItems {
    val executor: ScheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1)
    /*executor.execute(new Runnable {
      def run {
        LOG.fine("Start initItems")
        try {
          waitFor(jmsStarted)
          var sleepTime: Long = 100
          var items: Collection[StatusItem[_]] = null
          do {
            items = getter.getAllStatusItems
            if (items == null) {
              LOG.warning("Couldn't get StatusItems from StatusDB, sleeping...")
              Thread.sleep(sleepTime *= 2)
            }
          } while (items == null)
          import scala.collection.JavaConversions._
          for (item <- items) {
            update(item)
          }
        }
        catch {
          case e: Exception => {
            LOG.log(Level.SEVERE, e.getMessage, e)
          }
        }
        LOG.fine("End initItems")
      }
    })*/
  }

  def stop() {
  }

  def getName = name

  /**
   * Translate a StatusItem according to translations specified in the config file.
   *
   * @param item the item to translate
   * @return a Some<StatusItem<?>> with the translated item, or a None if a problem occured
   */
  def translate(item: StatusItem[_]): List[StatusItem[_]] = {
    val result = for {
        source <- translations.get(item.getName)
      } yield for {
          tr <- source
        } yield tr._2.translate(item.getValue)
    result.getOrElse(List.empty)
  }

}