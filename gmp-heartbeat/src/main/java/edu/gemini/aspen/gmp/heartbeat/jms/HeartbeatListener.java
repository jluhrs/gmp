package edu.gemini.aspen.gmp.heartbeat.jms;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class HeartbeatListener
 *
 * @author Nicolas A. Barriga
 *         Date: 12/29/10
 */
public class HeartbeatListener implements MessageListener {
    private static final Logger LOG = Logger.getLogger(HeartbeatListener.class.getName());
    private long last=0;

    public long getLast(){
        return last;
    }

    @Override
    public void onMessage(Message message) {
        if(message instanceof BytesMessage){
            BytesMessage bm=(BytesMessage)message;
            try{
                last=bm.readLong();
                LOG.info("Heartbeat: " + last);
            }catch(JMSException ex){
                LOG.log(Level.SEVERE,ex.getMessage(),ex);
            }
        }else{
            LOG.warning("Wrong message type");
        }
    }
}
