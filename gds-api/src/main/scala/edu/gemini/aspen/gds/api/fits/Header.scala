package edu.gemini.aspen.gds.api.fits

/**
 * Encapsulates a FITS header
 * @param index Index in the FITS file, 0 is the PDU
 * @param keywords set of keywords in its original ordering
 */
case class Header(val index: Int, val keywords: Seq[HeaderItem[_]], val fileOffset: Long = 0, val size:Long = 0, val dataSize: Long = 0) {
  def containsKey(key: String): Boolean = keywords exists {
    _.keywordName.key == key
  }
}