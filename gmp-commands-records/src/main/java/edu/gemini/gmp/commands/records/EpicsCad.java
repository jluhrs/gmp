package edu.gemini.gmp.commands.records;

import edu.gemini.gmp.top.Top;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.epics.api.Channel;
import edu.gemini.epics.api.ChannelListener;
import gov.aps.jca.CAException;
import gov.aps.jca.TimeoutException;

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
    private ChannelListener<String> attributeListener;

    private final List<Channel<String>> attributes = new ArrayList<Channel<String>>();
    private final ChannelAccessServer cas;

    EpicsCad(ChannelAccessServer cas) {
        this.cas = cas;
    }

    /**
     * Create Channels
     *
     * @param epicsTop
     * @param name              of the CAD. ex.: "observe"
     * @param attributeListener listener to be notified when any attribute is written to
     * @param dirListener       listener to be notified when the DIR field is written to
     * @param attributeNames    list of attribute names. Each will be an EPICS channel.
     */
    public synchronized void start(Top epicsTop, String name, ChannelListener<String> attributeListener, ChannelListener<Dir> dirListener, List<String> attributeNames) {
        LOG.info("EpicsCad start: " + epicsTop.buildEpicsChannelName(name));
        try {
            val = cas.createChannel(epicsTop.buildEpicsChannelName(name + ".VAL"), 0);
            clid = cas.createChannel(epicsTop.buildEpicsChannelName(name + ".ICID"), 0);
            dir = cas.createChannel(epicsTop.buildEpicsChannelName(name + ".DIR"), Dir.CLEAR);
            dir.registerListener(dirListener);
            mess = cas.createChannel(epicsTop.buildEpicsChannelName(name + ".MESS"), "");
            omss = cas.createChannel(epicsTop.buildEpicsChannelName(name + ".OMSS"), "");
            //ocid = cas.createChannel(top +":"+ name + ".OCID", 0);
            //mark = cas.createChannel(top +":"+ name + ".MARK", 0);
            this.attributeListener=attributeListener;
            for (String attribute : attributeNames) {
                Channel<String> ch = cas.createChannel(epicsTop.buildEpicsChannelName(attribute), "");
                ch.registerListener(attributeListener);
                attributes.add(ch);
            }
        } catch (CAException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    /**
     * Destroy Channels
     */
    public synchronized void stop() {
        LOG.info("EpicsCad stop: " + dir.getName());
        cas.destroyChannel(dir);
        cas.destroyChannel(val);
        cas.destroyChannel(mess);
        cas.destroyChannel(omss);
        cas.destroyChannel(clid);
        //cas.destroyChannel(ocid);
        //cas.destroyChannel(mark);
        for (Channel<String> ch : attributes) {
            cas.destroyChannel(ch);
        }
    }

    private Integer v = 0;
    private String m = "";

    /**
     * Set a value for the VAL field to be set to, after a post().
     *
     * @param v the value to set VAL to
     */
    public synchronized void setVal(Integer v) {
        this.v = v;
    }

    /**
     * Set a value for the MESS field to be set to, after a post().
     *
     * @param m the value to set MESS to
     */
    public synchronized void setMess(String m) {
        this.m = m;
    }

    /**
     * Write saved values for VAL and MESS EPICS fields.
     *
     * @throws CAException
     */
    public synchronized void post() throws CAException, TimeoutException {
        setMessage(m);
        m = "";
        val.setValue(v);
    }

    private void setMessage(String message) throws CAException, TimeoutException {
        String oldMessage = mess.getFirst();
        if (setIfDifferent(mess, message)) {
            omss.setValue(oldMessage);
        }
    }

    static private <T> boolean setIfDifferent(Channel<T> ch, T value) throws CAException, TimeoutException {
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
    public synchronized Integer getClid() {
        try {
            return clid.getFirst();
        } catch (CAException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
            return -1;
        } catch (TimeoutException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
            return -1;
        }
    }

    /**
     * Write DIR and CLID Channels
     *
     * @param d  Directive to write
     * @param id client ID
     * @throws CAException
     */
    public synchronized void setDir(Dir d, Integer id) throws CAException, TimeoutException {
        clid.setValue(id);
        dir.setValue(d);
    }

    /**
     * Get the CAD's message field
     *
     * @return message
     * @throws CAException
     */
    public String getMess() throws CAException, TimeoutException {
        return mess.getFirst();
    }

    /**
     * Register a listener to be notified when the VAL field changes
     *
     * @param listener to be notified
     */
    public void registerValListener(ChannelListener<Integer> listener) throws CAException {
        val.registerListener(listener);
    }

    /**
     * Unregister a listener for the VAL field
     *
     * @param listener to be unregistered
     */
    public void unRegisterValListener(ChannelListener<Integer> listener) throws CAException {
        val.unRegisterListener(listener);
    }

    /**
     * Get the attribute names and current values
     *
     * @return A Map containing names and values
     */
    public Map<String, String> getConfig() {
        Map<String, String> map = new HashMap<String, String>();
        for (Channel<String> ch : attributes) {
            try {
                //hack for observe and reboot configurations
                if (ch.getName().endsWith("DATA_LABEL")) {
                    map.put("DATA_LABEL", ch.getFirst());
                } else if (ch.getName().endsWith("REBOOT_OPT")) {
                    map.put("REBOOT_OPT", ch.getFirst());
                } else {
                    String value = ch.getFirst();
                    if (value.length() > 0) {
                        map.put(ch.getName(), value);
                    }
                }
            } catch (CAException e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
            } catch (TimeoutException e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return map;
    }

    /**
     * Clear the values of the attributes
     *
     * @return A Map containing names and values
     */
    public Map<String, String> clearConfig() {
        Map<String, String> map = new HashMap<String, String>();
        for (Channel<String> ch : attributes) {
            try {
                ch.unRegisterListener(attributeListener);
                ch.setValue("");
                ch.registerListener(attributeListener);
            } catch (CAException e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
            } catch (TimeoutException e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return map;
    }
}
