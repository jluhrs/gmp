package edu.gemini.aspen.gmp.heartbeat;

import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.giapi.util.jms.MessageBuilder;
import edu.gemini.jms.activemq.provider.ActiveMQJmsProvider;
import edu.gemini.jms.api.BaseMessageProducer;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;
import edu.gemini.jms.api.JmsProvider;

import javax.jms.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;


/**
 * Class Heartbeat
 *
 * @author Nicolas A. Barriga
 *         Date: 12/29/10
 */
public class Heartbeat extends BaseMessageProducer {
    private Timer timer;

    class HeartbeatSender extends TimerTask {
        private long counter=0;
        @Override
        public void run() {
            try {
                BytesMessage m = _session.createBytesMessage();
                m.writeLong(counter++);
                _producer.send(m);

            } catch (JMSException e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    public Heartbeat(){
        super("Heartbeat", new DestinationData(JmsKeys.GMP_HEARTBEAT_DESTINATION, DestinationType.TOPIC));

    }

    public void start(){
        try{
            startJms(new ActiveMQJmsProvider("tcp://localhost:61616"));
        }catch(JMSException ex){
            LOG.log(Level.SEVERE,ex.getMessage(),ex);
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new HeartbeatSender(),0,1000);
    }

    public void stop(){
        if(timer!=null){
            timer.cancel();
        }
        stopJms();
    }
}
