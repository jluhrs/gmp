package edu.gemini.aspen.gmp.commands.records;

import gov.aps.jca.CAException;

import java.util.List;

/**
 * Interface CADRecord
 *
 * @author Nicolas A. Barriga
 *         Date: 3/21/11
 */
public interface CADRecord {
    void setClid(Integer id) throws CAException;

    void setDir(Record.Dir d) throws CAException;

    Integer getVal() throws CAException;

    String getMess() throws CAException;
}
