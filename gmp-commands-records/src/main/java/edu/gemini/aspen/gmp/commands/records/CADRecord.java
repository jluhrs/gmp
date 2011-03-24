package edu.gemini.aspen.gmp.commands.records;

import edu.gemini.cas.ChannelListener;
import gov.aps.jca.CAException;

/**
 * Interface CADRecord
 *
 * @author Nicolas A. Barriga
 *         Date: 3/21/11
 */
public interface CADRecord {

    EpicsCad getEpicsCad();

    //void setDir(Dir dir, Integer id) throws CAException;


    //String getMess() throws CAException;

    //void registerValListener(ChannelListener listener);
    //void unRegisterValListener(ChannelListener listener);
    //void registerCARListener(CARRecord.CARListener listener);
    //void unRegisterCARListener(CARRecord.CARListener listener);
    //CARRecord getCAR();
    CARRecord getCAR();
}
