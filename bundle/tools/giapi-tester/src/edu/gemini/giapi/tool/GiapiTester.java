package edu.gemini.giapi.tool;

import edu.gemini.giapi.tool.commands.CommandSender;

import java.util.logging.Logger;

/**
 *
 */
public class GiapiTester {

    private static final Logger LOG = Logger.getLogger(GiapiTester.class.getName());


    public static void main(String[] args) {

        LOG.info("GIAPI Tester started");
        CommandSender sender = new CommandSender("tcp://localhost:61616");


    }

}
