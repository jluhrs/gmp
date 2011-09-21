package edu.gemini.aspen.gds.web.ui.security

/**
 * Default implementation of an encoder, that just returns the same text */
private class DefaultEncoder extends Encoder {
  def encode(value: String) = value
}





