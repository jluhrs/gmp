package edu.gemini.aspen.gds.api.fits

/**
 * Class representing a keyword key
 *
 * @param key The keyword key, must conform to being all capital and 1 to 8 items */
case class FitsKeyword(key:String) {
  require(key != null)
  require(key.matches(FitsKeyword.KEY_FORMAT))
}

object FitsKeyword {
  // Regular expression that FITS keyword must obey
  val KEY_FORMAT = """[\p{Upper}\d-_]{1,8}"""
}