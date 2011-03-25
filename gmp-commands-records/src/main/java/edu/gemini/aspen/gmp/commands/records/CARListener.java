package edu.gemini.aspen.gmp.commands.records;

/**
* Interface to monitor changes to a CAR record
*
* @author Nicolas A. Barriga
*         Date: 3/25/11
*/
interface CARListener {
    void update(CARRecord.Val state, String message, Integer errorCode, Integer id);
}
