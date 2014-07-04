package edu.gemini.gmp.commands.records;

/**
* Interface to monitor changes to a CAR record
*
* @author Nicolas A. Barriga
*         Date: 3/25/11
*/
interface CarListener {
    void update(CarRecord.Val state, String message, Integer errorCode, Integer id);
}
