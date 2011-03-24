package edu.gemini.aspen.gmp.commands.records;

import edu.gemini.cas.Channel;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.cas.ChannelListener;
import gov.aps.jca.CAException;
import gov.aps.jca.dbr.DBR;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class EpicsCad
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

    private List<Channel<String>> attributes = new ArrayList<Channel<String>>();

    EpicsCad(){}

    public synchronized void start(ChannelAccessServer cas, String prefix, String name, ChannelListener attributeListener, ChannelListener dirListener, List<String> attributeNames){
        LOG.info("EpicsCad start: "+prefix+":"+name);
        try {
            dir = cas.createChannel(prefix + ":" + name + ".DIR", Dir.CLEAR);
            dir.registerListener(dirListener);
            val = cas.createChannel(prefix + ":" + name + ".VAL", 0);
            mess = cas.createChannel(prefix + ":" + name + ".MESS", "");
            omss = cas.createChannel(prefix + ":" + name + ".OMSS", "");
            clid = cas.createChannel(prefix +":"+ name + ".ICID", 0);
            //ocid = cas.createChannel(prefix +":"+ name + ".OCID", 0);
            //mark = cas.createChannel(prefix +":"+ name + ".MARK", 0);
            for (String attribute: attributeNames) {
                Channel<String> ch = cas.createChannel(prefix +":"+ name + "." + attribute, "");
                ch.registerListener(attributeListener);
                attributes.add(ch);
            }
        } catch (CAException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
        }

    }
    public synchronized void stop(ChannelAccessServer cas){
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
    public synchronized void setVal(Integer v){
        this.v=v;
    }
    public synchronized void setMess(String m){
        this.m=m;
    }
    public synchronized void post() throws CAException {
        val.setValue(v);
        setMessage(m);
        m="";

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
    public synchronized Integer getClid(){
        try {
            return clid.getFirst();
        } catch (CAException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
            return -1;
        }
    }
    public synchronized void setDir(Dir d, Integer id) throws CAException {
        clid.setValue(id);
        dir.setValue(d);
    }

    public String getMess() throws CAException {
        return mess.getFirst();
    }

    public void registerValListener(ChannelListener listener) {
        val.registerListener(listener);
    }

    public void unRegisterValListener(ChannelListener listener) {
        val.unRegisterListener(listener);
    }

}
