package edu.gemini.giapi.tool;

import edu.gemini.giapi.tool.commands.CommandSender;
import edu.gemini.giapi.tool.jms.BrokerConnection;
import edu.gemini.aspen.gmp.commands.api.SequenceCommand;
import edu.gemini.aspen.gmp.commands.api.Activity;
import edu.gemini.aspen.gmp.commands.api.HandlerResponse;

import java.util.logging.Logger;

/**
 *
 */
public class GiapiTester {

    private static final Logger LOG = Logger.getLogger(GiapiTester.class.getName());


    public static void main(String[] args) {

        try {
            BrokerConnection connection = new BrokerConnection("tcp://localhost:61616");
            connection.start();

            CommandSender sender = new CommandSender(connection);

//            ClientCompletionListener listener = sender.getCompletionListener();

            HandlerResponse response = sender.send(SequenceCommand.INIT, Activity.PRESET, null);

            System.out.println("Response Received: " + response);

            if (response.getResponse() == HandlerResponse.Response.STARTED) {
                //now, wait for the answer, synchronously
                //listener.receiveMessage();
                sender.receiveCompletionInformation();
            }


            connection.stop();
        } catch (TesterException ex) {
            LOG.warning("Problem on GIAPI tester: " + ex.getMessage());
            ex.printStackTrace();
        }

    }

}
