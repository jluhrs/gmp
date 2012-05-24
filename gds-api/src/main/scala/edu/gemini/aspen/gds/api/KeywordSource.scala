package edu.gemini.aspen.gds.api

/**
 * List of possible sources for keyword values
 * NOT: If you add more sources here, check the gds-health-module */
object KeywordSource extends Enumeration {
  val SEQEXEC = Value //sent previously by seqexec
  val EPICS = Value //to be collected from EPICS channels
  val ODB = Value //to be collected from the ODB
  val STATUS = Value //sent previously by the instrument as StatusItem
  val CONSTANT = Value //constant value read from gds config file
  val PROPERTY = Value //value read out of a system or java property
  val INSTRUMENT = Value //value already written in the FITS file by the Instrument. GDS will check that these items are in place.

  val NONE = Value
}