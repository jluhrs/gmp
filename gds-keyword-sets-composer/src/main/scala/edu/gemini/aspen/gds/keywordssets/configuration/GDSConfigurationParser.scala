package edu.gemini.aspen.gds.keywordssets.configuration

import util.parsing.combinator.RegexParsers
import io.Source

class GDSConfigurationParser extends RegexParsers {
    override val skipWhitespace = false

    def lines = rep(line) ~ EOF 

    def line = (comment | configuration | CRLF)

    def configuration = spaces ~ instrument ~ spaces ~ instrument ~ spaces ~ instrument ~ spaces ~ instrument ~ spaces ~ instrument ~ spaces ~ spaces ~ instrument ~ spaces ~ spaces ~ instrument ~ spaces ~ spaces ~ instrument ~ spaces ~ spaces ~ instrument ~ spaces ~ fitscomment

    def instrument = """[:\w]*""".r

    def comment: Parser[String] = """#.*""".r

    def fitscomment = "\"" ~ internalComment ~ "\""

    def internalComment = """[^"]*""".r

    def spaces = opt(whitespace)

    def whitespace: Parser[String] = """[ \t]*""".r

    def CRLF = "\r\n" | "\n"

    def EOF = "\\z".r

    def parse(fileName: String) {
        val file = Source.fromFile(fileName, "UTF8")
        println(parseAll(lines, file.bufferedReader))
    }
}

object GDSConfigurationParser {
    def main(args: Array[String]) {
        val parser = new GDSConfigurationParser()
        parser.parse("src/main/resources/gds-keywords.conf")
    }
}