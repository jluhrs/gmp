package edu.gemini.aspen.gmp.epics.simulator.osgi;

import com.google.common.collect.Maps;
import edu.gemini.aspen.giapi.commands.CommandSender;
import edu.gemini.aspen.gmp.epics.EpicsRegistrar;
import edu.gemini.aspen.gmp.epics.simulator.EpicsSimulatorComponent;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ManagedServiceFactory;

import java.util.Dictionary;
import java.util.Map;
import java.util.logging.Logger;

public class EpicsSimulatorFactory implements ManagedServiceFactory {
    private static final Logger LOG = Logger.getLogger(EpicsSimulatorFactory.class.getName());
    private static final String CONFIG_FILE = "simulationConfiguration";

    private final Map<String, EpicsSimulatorComponent> existingServices = Maps.newHashMap();
    private final EpicsRegistrar registrar;

    public EpicsSimulatorFactory(EpicsRegistrar registrar) {
        this.registrar = registrar;
    }

    public String getName() {
        return "GMP Command Records factory";
    }

    @Override
    public void updated(String pid, Dictionary<String, ?> properties) {
        if (existingServices.containsKey(pid)) {
            existingServices.get(pid).readConfiguration();
            existingServices.remove(pid);
            updated(pid, properties);
        } else {
            if (checkProperties(properties)) {
                EpicsSimulatorComponent epicsService = createService(properties);
                epicsService.startSimulation();
                existingServices.put(pid, epicsService);
            } else {
                LOG.warning("Cannot build " + EpicsSimulatorComponent.class.getName() + " without the required properties");
            }
        }
    }

    private EpicsSimulatorComponent createService(Dictionary<String, ?> properties) {
        String configFile = properties.get(CONFIG_FILE).toString();
        return new EpicsSimulatorComponent(registrar, configFile);
    }

    private boolean checkProperties(Dictionary<String, ?> properties) {
        return properties.get(CONFIG_FILE) != null;
    }

    @Override
    public void deleted(String pid) {
        if (existingServices.containsKey(pid)) {
            existingServices.get(pid).stopSimulation();
            existingServices.remove(pid);
        }
    }

    public void stopServices() {
        for (String pid: existingServices.keySet()) {
            deleted(pid);
        }
    }

}
