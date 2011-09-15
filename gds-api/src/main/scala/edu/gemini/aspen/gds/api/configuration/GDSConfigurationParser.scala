package edu.gemini.aspen.gds.api.configuration

import util.parsing.combinator.RegexParsers
import io.Source
import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.giapi.data.{ObservationEvent, FitsKeyword}

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
    ~ spaces ~ defaultValue
    ~ spaces ~ subsystem
    ~ spaces ~ channelName
    ~ spaces ~ arrayIndex
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
      ~ s11 ~ fitsComment => GDSConfiguration(instrument, observationEvent, keyword, headerIndex, dataType, mandatory, nullValue, subsystem, channelName, arrayIndex, fitsComment)
  }

  def instrument = """\w+""".r ^^ {
    x => Instrument(x)
  }

  //Equivalent to: """OBS_PREP|OBS_START_ACQ|OBS_END_ACQ|OBS_START_READOUT|OBS_END_READOUT|OBS_START_DSET_WRITE|OBS_END_DSET_WRITE"""
  def observationEvent = (ObservationEvent.values().toList map {
    _.name
  } reduceLeft {
    (x, y) => x + "|" + y
  }).r ^^ {
    x => GDSEvent(x)
  }

  def keyword = """[\p{Upper}\d]{1,8}""".r ^^ {
    x => new FitsKeyword(x)
  }

  def headerIndex = """\d+""".r ^^ {
    x => HeaderIndex(x.toInt)
  }

  def datatype = """DOUBLE|INT|STRING""".r ^^ {
    x => DataType(x)
  }

  def mandatory = """[tTfF]""".r ^^ {
    case "F" => Mandatory(false)
    case "f" => Mandatory(false)
    case "T" => Mandatory(true)
    case "t" => Mandatory(true)
  }

  //todo:change to defaultValue
  def defaultValue = """\w+""".r ^^ {
    x => DefaultValue(x)
  }

  def subsystem = (KeywordSource.values map {
    _.toString
  } reduceLeft {
    (x, y) => x + "|" + y
  }).r ^^ {
    x => Subsystem(KeywordSource.withName(x))
  }

  def channelName = """[:\w]+""".r ^^ {
    x => Channel(x)
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
