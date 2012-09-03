package edu.gemini.aspen.gds.api.configuration

import util.parsing.combinator.RegexParsers
import io.Source
import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.giapi.data.ObservationEvent
import fits.FitsKeyword
import edu.gemini.aspen.gds.api.Conversions._

case class Space(length: Int)

case class Comment(comment: String) {
  override def toString: String = comment
}

class GDSConfigurationParser extends RegexParsers {
  override val skipWhitespace = false

  def lines = rep1sep(line, whitespace ~ CRLF) <~ (whitespace ~ EOF)

  def line = opt(comment | configuration)

  def configuration = (spaces ~ instrument
    ~ spaces ~ observationEvent
    ~ spaces ~ keyword
    ~ spaces ~ headerIndex
    ~ spaces ~ datatype
    ~ spaces ~ mandatory
    ~ spaces ~ (defaultValueNonQuotes | defaultValueInQuotes)
    ~ spaces ~ subsystem
    ~ spaces ~ channelName
    ~ spaces ~ arrayIndex
    ~ spaces ~ format
    ~ spaces ~ fitscomment) ^^ {
    case s1 ~ instrument
      ~ s2 ~ observationEvent
      ~ s3 ~ keyword
      ~ s4 ~ headerIndex
      ~ s5 ~ dataType
      ~ s6 ~ mandatory
      ~ s7 ~ nullValue
      ~ s8 ~ subsystem
      ~ s9 ~ channelName
      ~ s10 ~ arrayIndex
      ~ s11 ~ format
      ~ s12 ~ fitsComment => GDSConfiguration(instrument, observationEvent, keyword, headerIndex, dataType, mandatory, nullValue, subsystem, channelName, arrayIndex, format, fitsComment)
  }

  def instrument = """\w+""".r ^^ {
    x => Instrument(x)
  }

  //Equivalent to: """OBS_PREP|OBS_START_ACQ|OBS_END_ACQ|OBS_START_READOUT|OBS_END_READOUT|OBS_START_DSET_WRITE|OBS_END_DSET_WRITE|EXT_START_OBS|EXT_END_OBS"""
  def observationEvent = (ObservationEvent.values().toList map {
    _.name
  } reduceLeft {
    (x, y) => x + "|" + y
  }).r ^^ {
    x => GDSEvent(x)
  }

  def keyword = FitsKeyword.KEY_FORMAT.r ^^ {
    x => new FitsKeyword(x)
  }

  def headerIndex = """\d+""".r ^^ {
    x => HeaderIndex(x.toInt)
  }

  def datatype = (FitsType.TypeNames.values.toList map {
    _.toString
  } reduceLeft {
    (x, y) => x + "|" + y
  }).r ^^ {
    x => DataType(x)
  }

  def mandatory = """[tTfF]""".r ^^ {
    case "F" => Mandatory(false)
    case "f" => Mandatory(false)
    case "T" => Mandatory(true)
    case "t" => Mandatory(true)
  }

  // Default value can be anything that does not contain spaces or is is quotes
  def defaultValue = defaultValueInQuotes | defaultValueNonQuotes

  def defaultValueNonQuotes = """[^'"\s]+""".r ^^ {
    x: String => DefaultValue(x)
  }

  def defaultValueInQuotes = "\"" ~> internalComment <~ "\"" ^^ {
    x: String => DefaultValue(x)
  }

  def subsystem = (KeywordSource.values map {
    _.toString
  } reduceLeft {
    (x, y) => x + "|" + y
  }).r ^^ {
    x => Subsystem(KeywordSource.withName(x))
  }

  // Default value can be anything that does not contain a space
  def channelName = """[:\w\.\]\[]+""".r ^^ {
    x: String => Channel(x)
  }

  def arrayIndex = """\d+""".r ^^ {
    x => ArrayIndex(x.toInt)
  }

  def comment = spaces ~> """#.*""".r ^^ {
    x => Comment(x.trim)
  }

  def fitscomment = "\"" ~> internalComment <~ "\"" ^^ {
    x => FitsComment(x)
  }

  def format = "\"" ~> GDSConfigurationParser.internalFormat <~ "\"" ^^ {
    x => stringToFormat(x)
  }

  def internalComment = """[^"]*""".r


  def spaces = opt(whitespace) ^^ {
    case Some(spaces) => Space(spaces.length)
    case None => Space(0)
  }

  def whitespace = """[ \t]*""".r

  def CRLF = "\r\n" | "\n"

  def EOF = "\\z".r

  def parseFile(fileName: String) = {
    parseFileRawResult(fileName).get collect {
      case Some(x) => x
    }
  }

  def parseFileRawResult(fileName: String) = {
    val file = Source.fromFile(fileName, "UTF8")
    parseAll(lines, file.bufferedReader)
  }

  def parseText(text: String) = {
    parseAll(lines, text)
  }
}

object GDSConfigurationParser {
  //todo: improve internal format, for now we accept anything that doesn't include quotes and does include %
  def internalFormat = """([^"]*%[^"]*)|()""".r

}