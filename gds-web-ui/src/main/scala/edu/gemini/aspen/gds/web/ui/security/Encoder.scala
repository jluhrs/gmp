package edu.gemini.aspen.gds.web.ui.security

/**
 * Interface definition for an object capable of encoding a password */
trait Encoder {
  /**
   * Returns the value encoded as defined by the encoder */
  def encode(value: String): String
}