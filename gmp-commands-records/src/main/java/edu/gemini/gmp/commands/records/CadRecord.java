package edu.gemini.gmp.commands.records;

/**
 * This is the interface exposed by the CAD records to OSGI. The apply record uses it to gain access to the CAD and CAR
 * EPICS records.
 *
 * @author Nicolas A. Barriga
 *         Date: 3/21/11
 */
public interface CadRecord {
    /**
     * Get the EPICS CAD.
     *
     * @return a class representing the EPICS part of the CAD
     */
    EpicsCad getEpicsCad();

    /**
     * Get the CAR record
     *
     * @return the CAR record associated with this CAD
     */
    CarRecord getCar();

    /**
     * Create the EPICS channels
     */
    void start();

    /**
     * Destroy the EPICS channels.
     */
    void stop();
}
