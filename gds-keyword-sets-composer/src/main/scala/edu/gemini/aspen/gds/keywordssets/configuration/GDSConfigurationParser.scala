package edu.gemini.aspen.gds.keywordssets.configuration

import util.parsing.combinator.RegexParsers
import io.Source

case class Comment(comment:String)

class GDSConfigurationParser extends RegexParsers {
    override val skipWhitespace = false

    case class Instrument(name:String)
    case class ObservationEvent(name:String)
    case class Keyword(name:String)
    case class DataType(name:String)
    case class Mandatory(value:Boolean)
    case class NullValue(value:String)
    case class Subsystem(value:String)
    case class Channel(value:String)
    case class ArrayIndex(value:String)
    case class Space(length:Int)

    def lines = rep(line) <~ EOF 

    def line = (comment | configuration | CRLF)

    def configuration = (spaces ~ instrument
        ~ spaces ~ observationEvent
        ~ spaces ~ keyword
        ~ spaces ~ datatype
        ~ spaces ~ mandatory
        ~ spaces ~ nullValue
        ~ spaces ~ subsystem
        ~ spaces ~ channelName
        ~ spaces ~ arrayIndex
        ~ spaces ~ fitscomment)

    def instrument = """\w*""".r ^^ {x => Instrument(x)}

    def observationEvent = """[\p{Upper}_]*""".r ^^ {x => ObservationEvent(x)}

    def keyword = """[\p{Upper}\d]{1,8}""".r ^^ {x => Keyword(x)}

    def datatype = "DOUBLE" ^^ {x => DataType(x)}

    def mandatory = """[tTfF]""".r ^^ {
        case "F" => Mandatory(false)
        case "f" => Mandatory(false)
        case "T" => Mandatory(true)
        case "t" => Mandatory(true)
    }

    def nullValue = """\w*""".r ^^ {x => NullValue(x)}

    def subsystem = """\w*""".r ^^ {x => Subsystem(x)}

    def channelName = """[:\w]*""".r ^^ {x => Channel(x)}

    def arrayIndex = """\w*""".r ^^ {x => ArrayIndex(x)}

    def comment = """#.*""".r ^^ {x => Comment(x)}

    def fitscomment = "\"" ~> internalComment <~ "\""

    def internalComment = """[^"]*""".r

    def spaces = opt(whitespace)

    def whitespace: Parser[String] = """[ \t]*""".r

    def CRLF = "\r\n" | "\n"

    def EOF = "\\z".r

    def parseFile(fileName: String) {
        val file = Source.fromFile(fileName, "UTF8")
        println(parseAll(lines, file.bufferedReader))
    }

    def parseText(text: String) = {
        parseAll(lines, text)
    }
}

object GDSConfigurationParser {
    def main(args: Array[String]) {
        val parser = new GDSConfigurationParser()
        parser.parseFile("src/main/resources/gds-keywords.conf")
    }
}