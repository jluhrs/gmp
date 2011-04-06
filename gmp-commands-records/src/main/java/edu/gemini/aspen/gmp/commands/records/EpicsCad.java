package edu.gemini.aspen.gmp.commands.records;

import edu.gemini.aspen.gmp.epics.top.EpicsTop;
import edu.gemini.cas.Channel;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.cas.ChannelListener;
import gov.aps.jca.CAException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents the EPICS part of the CAD record. It doesn't have much logic, it is just to reflect the
 * CadRecordImpl status and communicate to the outside world.
 *
 * @author Nicolas A. Barriga
 *         Date: 3/24/11
 */
public class EpicsCad {
    private static final Logger LOG = Logger.getLogger(EpicsCad.class.getName());

    private Channel<Dir> dir;
    private Channel<Integer> val;
    private Channel<String> mess;
    private Channel<String> omss;
    private Channel<Integer> clid;
    //private Channel<Integer> ocid;
    //private Channel<Integer> mark;

    private final List<Channel<String>> attributes = new ArrayList<Channel<String>>();
    private final ChannelAccessServer cas;
    EpicsCad(ChannelAccessServer cas){
        this.cas=cas;
    }

    /**
     * Create Channels
     *
     * @param epicsTop
     * @param name of the CAD. ex.: "observe"
     * @param attributeListener listener to be notified when any attribute is written to
     * @param dirListener listener to be notified when the DIR field is written to
     * @param attributeNames list of attribute names. Each will be an EPICS channel.
     */
    public synchronized void start(EpicsTop epicsTop, String name, ChannelListener attributeListener, ChannelListener dirListener, List<String> attributeNames){
        LOG.info("EpicsCad start: "+ epicsTop.buildChannelName(name));
        try {
            val = cas.createChannel(epicsTop.buildChannelName(name + ".VAL"), 0);
            clid = cas.createChannel(epicsTop.buildChannelName(name + ".ICID"), 0);
            dir = cas.createChannel(epicsTop.buildChannelName(name + ".DIR"), Dir.CLEAR);
            dir.registerListener(dirListener);
            mess = cas.createChannel(epicsTop.buildChannelName(name + ".MESS"), "");
            omss = cas.createChannel(epicsTop.buildChannelName(name + ".OMSS"), "");
            //ocid = cas.createChannel(top +":"+ name + ".OCID", 0);
            //mark = cas.createChannel(top +":"+ name + ".MARK", 0);
            for (String attribute: attributeNames) {
                Channel<String> ch = cas.createChannel(epicsTop.buildChannelName(attribute), "");
                ch.registerListener(attributeListener);
                attributes.add(ch);
            }
        } catch (CAException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    /**
     * Destroy Channels
     *
     */
    public synchronized void stop(){
        LOG.info("EpicsCad stop: "+dir.getName());
        cas.destroyChannel(dir);
        cas.destroyChannel(val);
        cas.destroyChannel(mess);
        cas.destroyChannel(omss);
        cas.destroyChannel(clid);
        //cas.destroyChannel(ocid);
        //cas.destroyChannel(mark);
        for(Channel<String> ch:attributes){
            cas.destroyChannel(ch);
        }
    }
    private Integer v=0;
    private String m="";

    /**
     * Set a value for the VAL field to be set to, after a post().
     *
     * @param v the value to set VAL to
     */
    public synchronized void setVal(Integer v){
        this.v=v;
    }

    /**
     * Set a value for the MESS field to be set to, after a post().
     *
     * @param m the value to set MESS to
     */
    public synchronized void setMess(String m){
        this.m=m;
    }

    /**
     * Write saved values for VAL and MESS EPICS fields.
     *
     * @throws CAException
     */
    public synchronized void post() throws CAException {
        setMessage(m);
        m="";
        val.setValue(v);
    }
    private void setMessage(String message) throws CAException {
        String oldMessage = mess.getFirst();
        if (setIfDifferent(mess, message)) {
            omss.setValue(oldMessage);
        }
    }
    static private <T> boolean setIfDifferent(Channel<T> ch, T value) throws CAException {
        if (!value.equals(ch.getFirst())) {
            ch.setValue(value);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get the CAD's client ID
     *
     * @return client ID
     */
    public synchronized Integer getClid(){
        try {
            return clid.getFirst();
        } catch (CAException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
            return -1;
        }
    }

    /**
     * Write DIR and CLID Channels
     *
     * @param d Directive to write
     * @param id client ID
     * @throws CAException
     */
    public synchronized void setDir(Dir d, Integer id) throws CAException {
        clid.setValue(id);
        dir.setValue(d);
    }

    /**
     * Get the CAD's message field
     *
     * @return message
     * @throws CAException
     */
    public String getMess() throws CAException {
        return mess.getFirst();
    }

    /**
     * Register a listener to be notified when the VAL field changes
     *
     * @param listener to be notified
     */
    public void registerValListener(ChannelListener listener) {
        val.registerListener(listener);
    }

    /**
     * Unregister a listener for the VAL field
     *
     * @param listener to be unregistered
     */
    public void unRegisterValListener(ChannelListener listener) {
        val.unRegisterListener(listener);
    }

    /**
     * Get the attribute names and current values
     *
     * @return  A Map containing names and values
     */
    public Map<String,String> getConfig(){
        Map<String,String> map = new HashMap<String,String>();
        for(Channel<String> ch: attributes){
            try {
                map.put(ch.getName(),ch.getFirst());
            } catch (CAException e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return map;
    }
}
