package edu.gemini.aspen.gds.web.ui.security

import java.security.MessageDigest

/**
 * Implementation of a simple encoder using SHA-1, no salt being applied
 *
 * The encoded value is returned in HEX encoding
 */
private class SHA1Encoder extends Encoder {
  val sha = MessageDigest.getInstance("SHA1")
  def encode(value: String) = {
    val d = sha.digest(value.getBytes)
    // Return in hex
    d map {
      "%02x".format(_)
    } mkString
  }
}