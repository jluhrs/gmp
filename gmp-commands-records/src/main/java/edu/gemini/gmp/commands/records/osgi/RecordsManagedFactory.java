package edu.gemini.gmp.commands.records.osgi;

import com.google.common.collect.Maps;
import edu.gemini.aspen.giapi.commands.CommandSender;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.gmp.commands.records.CommandRecordsBuilder;
import edu.gemini.gmp.top.Top;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ManagedServiceFactory;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

public class RecordsManagedFactory implements ManagedServiceFactory {
    private static final Logger LOG = Logger.getLogger(RecordsManagedFactory.class.getName());
    private static final String CONFIG_FILE = "xmlFileName";

    private final BundleContext context;
    private final ChannelAccessServer cas;
    private final CommandSender cs;
    private final Top top;
    private final Map<String, CommandRecordsBuilder> existingServices = Maps.newHashMap();

    public RecordsManagedFactory(BundleContext context, ChannelAccessServer cas, CommandSender cs, Top top) {
        this.context = context;
        this.cas = cas;
        this.cs = cs;
        this.top = top;
    }

    public String getName() {
        return "GMP Command Records factory";
    }

    @Override
    public void updated(String pid, Dictionary<String, ?> properties) {
        if (existingServices.containsKey(pid)) {
            existingServices.get(pid).stop();
            existingServices.remove(pid);
            updated(pid, properties);
        } else {
            if (checkProperties(properties)) {
                CommandRecordsBuilder epicsService = createService(properties);
                epicsService.start();
                existingServices.put(pid, epicsService);
            } else {
                LOG.warning("Cannot build " + CommandRecordsBuilder.class.getName() + " without the required properties");
            }
        }
    }

    private CommandRecordsBuilder createService(Dictionary<String, ?> properties) {
        String configFile = properties.get(CONFIG_FILE).toString();
        return new CommandRecordsBuilder(cas, cs, top, configFile);
    }

    private boolean checkProperties(Dictionary<String, ?> properties) {
        return properties.get(CONFIG_FILE) != null;
    }

    @Override
    public void deleted(String pid) {
        if (existingServices.containsKey(pid)) {
            existingServices.get(pid).stop();
            existingServices.remove(pid);
        }
    }

    public void stopServices() {
        for (String pid: existingServices.keySet()) {
            deleted(pid);
        }
    }

}
