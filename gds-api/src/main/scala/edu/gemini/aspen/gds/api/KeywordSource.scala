package edu.gemini.aspen.gds.api


object KeywordSource extends Enumeration {
    val SEQEXEC = Value //sent previously by seqexec
    val EPICS = Value //to be collected from EPICS channels
    val ODB = Value //to be collected from the ODB
    val STATUS = Value //sent previously by the instrument as StatusItem
    val CONSTANT = Value //constant value read from gds config file
    val IFS = Value //value already written in the FITS file by the IFS. GDS will check that these items are in place.

    val NONE = Value
}