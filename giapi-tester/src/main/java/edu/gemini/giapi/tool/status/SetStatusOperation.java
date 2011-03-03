package edu.gemini.giapi.tool.status;

import edu.gemini.aspen.giapi.status.AlarmCause;
import edu.gemini.aspen.giapi.status.AlarmSeverity;
import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapitestsupport.StatusSetter;
import edu.gemini.giapi.tool.arguments.*;
import edu.gemini.giapi.tool.parser.Argument;
import edu.gemini.giapi.tool.parser.Operation;
import edu.gemini.jms.activemq.provider.ActiveMQJmsProvider;
import edu.gemini.jms.api.JmsProvider;

import javax.jms.JMSException;
import java.util.logging.Logger;

/**
 * The operation to get the last value of a status item from the GMP.
 */
public class SetStatusOperation implements Operation {

    private static final Logger LOG = Logger.getLogger(SetStatusOperation.class.getName());

    private String _name;
    private String _value;
    private SetStatusArgument.StatusType _type;
    private AlarmSeverity _severity;
    private AlarmCause _cause;
    private String _message="";

    private String _host = "localhost";

    public void setArgument(Argument arg) {
        if (arg instanceof SetStatusArgument) {
            //_statusItem = ((SetStatusArgument)arg).getStatusItem();
            _name = ((SetStatusArgument)arg).getName();
        }
        if(arg instanceof TypeArgument){
            _type = ((TypeArgument)arg).getType();
        }
        if(arg instanceof ValueArgument){
            _value = ((ValueArgument)arg).getValue();
        }
        if(arg instanceof AlarmSeverityArgument){
            _severity = ((AlarmSeverityArgument)arg).getSeverity();
        }
        if(arg instanceof AlarmCauseArgument){
            _cause = ((AlarmCauseArgument)arg).getCause();
        }
        if(arg instanceof AlarmMessageArgument){
            _message = ((AlarmMessageArgument)arg).getMessage();
        }
        if (arg instanceof HostArgument) {
            _host = ((HostArgument)arg).getHost();
        }
    }

    public boolean isReady() {
        return (_name != null)&&(_value != null)&&(_type!=null);
    }

    public void execute() throws Exception {
        JmsProvider provider = new ActiveMQJmsProvider("tcp://" + _host + ":61616");
        StatusItem _statusItem = null;
        if(_type.ordinal()>4 && (_severity != null) && (_cause != null )){
            _statusItem = _type.getStatusItem(_name,_value,_severity,_cause,_message);
        }else if(_type.ordinal()<=4 && (_severity == null) && (_cause == null )){
            _statusItem = _type.getStatusItem(_name,_value);
        }else{
            LOG.severe("If you indicate an alarm type, you must indicate severity and cause (message is optional).");
            return;
        }
        StatusSetter setter = new StatusSetter(_statusItem.getName());


        try {
            setter.startJms(provider);
            
            setter.setStatusItem(_statusItem);



        }  catch (JMSException ex) {
            LOG.severe("Problem on GIAPI tester: " + ex.getMessage());
        } finally{
             setter.stopJms();
        }
    }
}
