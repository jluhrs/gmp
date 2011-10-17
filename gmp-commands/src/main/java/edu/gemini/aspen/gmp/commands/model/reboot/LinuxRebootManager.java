package edu.gemini.aspen.gmp.commands.model.reboot;

import edu.gemini.aspen.giapi.commands.*;
import edu.gemini.aspen.gmp.commands.model.RebootManager;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class just prints a Log message that the reboot sequence command
 * will be executed.
 */
public class LinuxRebootManager implements RebootManager {
    private static final Logger LOG = Logger.getLogger(LinuxRebootManager.class.getName());
    private String instrumentStartupScript;

    public LinuxRebootManager(String instrumentStartupScript) {
        LOG.fine("Constructing reboot manager with script: " + instrumentStartupScript);
        this.instrumentStartupScript = instrumentStartupScript;
    }

    @Override
    synchronized public void reboot(RebootArgument arg) {
        LOG.info("Starting shutdown of the instrument with argument " + arg);
        Runtime runtime = Runtime.getRuntime();
        switch (arg) {
            //tlc 80 10
            //gmp 75 15
            case REBOOT:
                //chkconfig off gpitlcd gmp-server
                LOG.info("chkconfig " + instrumentStartupScript + " off");
                chkconfig(instrumentStartupScript, "off");
                LOG.info("chkconfig gmp-server off");
                chkconfig("gmp-server", "off");

                //call gpitlcd reboot
                LOG.info("/etc/init.d/" + instrumentStartupScript + " reboot");
                execute("/etc/init.d/" + instrumentStartupScript + " reboot");

                //call gmp-server stop
                LOG.info("/etc/init.d/gmp-server stop");
                execute("/etc/init.d/gmp-server stop");

                //reboot
                LOG.info("/sbin/shutdown -r now");
                execute("/sbin/shutdown -r now");
                break;
            case GMP:
                //chkconfig on  gmp-server
                LOG.info("chkconfig --level 345 gmp-server on");
                chkconfig("gmp-server", "on");

                //chkconfig off gpitlcd
                LOG.info("chkconfig gpi-tlcd off");
                chkconfig(instrumentStartupScript, "off");

                //call gpitlcd reboot
                LOG.info("/etc/init.d/" + instrumentStartupScript + " reboot");
                execute("/etc/init.d/" + instrumentStartupScript + " reboot");

                //call gmp-server stop
                LOG.info("/etc/init.d/gmp-server stop");
                execute("/etc/init.d/gmp-server stop");

                //reboot
                LOG.info("/sbin/shutdown -r now");
                execute("/sbin/shutdown -r now");
                break;
            case NONE:
                //chkconfig on gpitlcd gmp-server
                LOG.info("chkconfig --level 345 gpi-tlcd on");
                chkconfig(instrumentStartupScript, "on");
                LOG.info("chkconfig --level 345 gmp-server on");
                chkconfig("gmp-server", "on");

                //call gpitlcd reboot
                LOG.info("/etc/init.d/" + instrumentStartupScript + " reboot");
                execute("/etc/init.d/" + instrumentStartupScript + " reboot");

                //call gmp-server stop
                LOG.info("/etc/init.d/gmp-server stop");
                execute("/etc/init.d/gmp-server stop");

                //reboot
                LOG.info("/sbin/shutdown -r now");
                execute("/sbin/shutdown -r now");
                break;
        }
    }

    private void execute(String command) {
        try {
            Process p = Runtime.getRuntime().exec("sudo " + command);
            if (p.waitFor() != 0) {
                LOG.severe("\"" + command + "\" failed. Exit value: " + p.exitValue() + ", error message: " + errorMessage(p));
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        } catch (InterruptedException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private void chkconfig(String script, String action) {
        try {
            Process p = Runtime.getRuntime().exec("sudo /sbin/chkconfig --level 345 " + script + " " + action);
            if (p.waitFor() != 0) {
                LOG.severe("Chkconfig " + action + " for " + script + " failed. Exit value: " + p.exitValue() + ", error message: " + errorMessage(p));
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        } catch (InterruptedException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private String errorMessage(Process p) throws IOException {
        int available = p.getErrorStream().available();
        if (available > 0) {
            byte[] msg = new byte[available];
            p.getErrorStream().read(msg);
            return new String(msg);
        } else {
            return "";
        }
    }
}
