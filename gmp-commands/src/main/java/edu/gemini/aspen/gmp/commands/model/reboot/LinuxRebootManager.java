package edu.gemini.aspen.gmp.commands.model.reboot;

import edu.gemini.aspen.giapi.commands.*;
import edu.gemini.aspen.gmp.commands.model.Action;
import edu.gemini.aspen.gmp.commands.model.ActionMessageBuilder;
import edu.gemini.aspen.gmp.commands.model.ActionSender;
import edu.gemini.aspen.gmp.commands.model.RebootManager;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class just prints a Log message that the reboot sequence command
 * will be executed.
 */
public class LinuxRebootManager implements RebootManager {
    private static final Logger LOG = Logger.getLogger(LinuxRebootManager.class.getName());
    private CountDownLatch _latch;
    private CommandSender _sender;

    public LinuxRebootManager(CommandSender sender) {
        _sender = sender;
    }

    @Override
    synchronized public void reboot(RebootArgument arg) {
        LOG.info("Starting shutdown of the instrument with argument " + arg);
        _latch = new CountDownLatch(1);
        switch (arg) {
            //tlc 80 10
            //gmp 75 15
            case REBOOT:
                //send park command
                park();
                //chkconfig off gpitlcd gmp-server
                LOG.info("chkconfig gpi-tlcd off");
                LOG.info("chkconfig gmp-server off");
                //call gpitlcd reboot
                LOG.info("/etc/init.d/gpi-tlcd reboot");
                //reboot
                LOG.info("shutdown -r now");
                break;
            case GMP:
                //send park command
                park();
                //chkconfig on  gmp-server
                LOG.info("chkconfig --level 345 gmp-server on");
                //chkconfig off gpitlcd
                LOG.info("chkconfig gpi-tlcd off");
                //call gpitlcd reboot
                LOG.info("/etc/init.d/gpi-tlcd reboot");
                //reboot
                LOG.info("shutdown -r now");
                break;
            case NONE:
                //send park command
                park();
                //chkconfig on gpitlcd gmp-server
                LOG.info("chkconfig --level 345 gpi-tlcd on");
                LOG.info("chkconfig --level 345 gmp-server on");
                //call gpitlcd reboot
                LOG.info("/etc/init.d/gpi-tlcd reboot");
                //reboot
                LOG.info("shutdown -r now");
                break;
        }
    }

    private void park() {
        HandlerResponse resp = _sender.sendCommand(
                new Command(SequenceCommand.PARK,
                        Activity.PRESET_START),
                new CompletionListener() {
                    @Override
                    public void onHandlerResponse(HandlerResponse response, Command command) {
                        if (!response.getResponse().equals(HandlerResponse.Response.COMPLETED)) {
                            LOG.severe("PARK command failed. Got: " + response);
                        }
                        _latch.countDown();
                    }
                });

        switch (resp.getResponse()) {
            case STARTED:
                try {
                    //wait up to a minute for park command to finish
                    //todo:change to 1 minute or more
                    if (!_latch.await(10, TimeUnit.SECONDS)) {
                        LOG.severe("Timed out while waiting for PARK to complete");
                    }
                } catch (InterruptedException e) {
                    LOG.log(Level.SEVERE, e.getMessage(), e);
                }
                break;
            case ERROR:
            case NOANSWER:
                LOG.severe("Error parking instrument: " + resp.getMessage());
                break;
            case ACCEPTED:
            case COMPLETED:
                LOG.info("Instrument parked");
                break;

        }

    }


}
