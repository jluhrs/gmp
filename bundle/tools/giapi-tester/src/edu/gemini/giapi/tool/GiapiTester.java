package edu.gemini.giapi.tool;

import edu.gemini.giapi.tool.commands.CommandSender;
import edu.gemini.giapi.tool.jms.BrokerConnection;
import edu.gemini.aspen.gmp.commands.api.*;

import java.util.logging.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

/**
 *
 */
public class GiapiTester {

    private static final Logger LOG = Logger.getLogger(GiapiTester.class.getName());

    enum Operation {
        SEQUENCE_COMMAND
    }

    public static void main(String[] args) throws Exception {

        boolean help = false;
        Operation operation = null;
        int repetitions = 1;

        SequenceCommand sc = null;
        Activity activity = null;
        Configuration config = null;

        int i = 0, j;
        String opt;
        while (i < args.length) {
            opt = args[i++];
            // All options start with a
            if (opt.charAt(0) != '-')
                die("I think you forgot a dash somewhere. I don't understand the argument " + opt);


            // Remove the - from the command
            String key = opt.substring(1, opt.length());
            if (key.length() == 0) {
                die("I don't understand empty dashes.");
            }

            //sequence command
            if (key.equals("sc")) {
                if (i < args.length) {
                    String val = args[i++];
                    try {
                        sc = SequenceCommand.valueOf(val);
                    } catch (IllegalArgumentException ex) {
                        die("Illegal sequence command: " + val + ".\nOptions are: " + getValues(SequenceCommand.class));
                    }
                } else {
                    die("What sequence command? Try -sc <command>");
                }
            } else
                //activity
                if (key.equals("activity")) {
                    if (i < args.length) {
                        String val = args[i++];
                        try {
                            activity = Activity.valueOf(val);
                        } catch (IllegalArgumentException ex) {
                            die("Illegal activity: " + activity + ".\nOptions are: " + getValues(Activity.class));
                        }
                    } else {
                        die("What activity? Try -activity <activity>");
                    }
                } else
                    //configuration
                    if (key.equals("config")) {
                        if (i < args.length) {
                            String val = args[i++];
                            try {
                                config = _parseConfiguration(config, val);
                            } catch (IllegalArgumentException ex) {
                                die("Illegal confguration: " + val + " (" + ex.getMessage() + ")");
                            }
                        } else {
                            die("What configuration? Try -config <configuration>");
                        }
                    } else
                        //repetitions
                        if (key.equals("r")) {
                            if (i < args.length) {
                                String val = args[i++];
                                try {
                                    repetitions = Integer.parseInt(val);
                                } catch (NumberFormatException ex) {
                                    die("Repetitions has to be an integer number. Try -r <repetitions>");
                                }
                                if (repetitions <= 0) {
                                    die("If you want to repeat, you have to specify a number bigger than 0");
                                }
                            } else {
                                die("You have to tell me how many repetitions you want. Try -r <repetitions>");
                            }

                        }
                        //now we process here the flag arguments
                        else {
                            char flag;
                            for (j = 0; j < key.length(); j++) {
                                flag = key.charAt(j);
                                switch (flag) {
                                    case '?':
                                        help = true;
                                        break;
                                    default:
                                        die("Illegal option: -" + flag);
                                        break;
                                }
                            }
                        }

        }

        if (help) {
            usage();
            return;
        }

        if (sc != null && activity != null) {
            operation = Operation.SEQUENCE_COMMAND;
        }

        if (operation == null)
            die("Sorry, what operation do you want to test?");

        switch (operation) {
            case SEQUENCE_COMMAND:
                BrokerConnection connection = new BrokerConnection("tcp://localhost:61616");
                try {

                    connection.start();

                    CommandSender sender = new CommandSender(connection);

                    for (int x = 0; x < repetitions; x++) {
                        HandlerResponse response = sender.send(sc, activity, config);

                        System.out.println("Response Received: " + response);

                        if (response.getResponse() == HandlerResponse.Response.STARTED) {
                            //now, wait for the answer, synchronously
                            CompletionInformation info = sender.receiveCompletionInformation();
                            System.out.println("Completion Information: " + info);
                        }
                    }
                } catch (TesterException ex) {
                    LOG.warning("Problem on GIAPI tester: " + ex.getMessage());
                    ex.printStackTrace();
                    break;
                } finally {
                    connection.stop();
                }
                break;
            default:
                die("Sorry, what operation do you want to test?");
        }
    }

    private static Configuration _parseConfiguration(Configuration config, String val) throws IllegalArgumentException {

        if (val == null)
            throw new IllegalArgumentException("Empty configuration");

        String[] items = val.split("\\s");
        for (String item : items) {
            String[] arg = item.split("=");
            if (arg.length != 2)
                throw new IllegalArgumentException("Configuration item '" + item + "' not in the form 'key=value'");
            if (config == null)
                config = new DefaultConfiguration();
            DefaultConfiguration dc = (DefaultConfiguration) config;
            dc.put(new ConfigPath(arg[0]), arg[1]);
        }
        return config;
    }

    private static <T extends Enum<T>> String getValues(Class<T> e) {
        StringBuilder sb = new StringBuilder();

        T[] elems = e.getEnumConstants();
        for (int i = 0; i < elems.length; i++) {
            sb.append(elems[i]);
            if (i < elems.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }


    private static void die(String err) {
        System.err.println(err);
        System.err.println("Let me help you. Try: java -jar giapi-tester.jar -?");
        System.exit(-1);
    }

    private static void usage() throws IOException {
        show(GiapiTester.class.getResourceAsStream("usage.txt"));
    }

    private static void show(InputStream is) throws IOException {
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        for (String line = br.readLine(); line != null; line = br.readLine())
            System.out.println(line);
    }

}
