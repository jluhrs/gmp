package edu.gemini.aspen.gmp.status.simulator;

import edu.gemini.aspen.gmp.status.simulator.generated.ObjectFactory;
import edu.gemini.aspen.gmp.status.simulator.generated.SimulatedStatusesType;
import edu.gemini.aspen.gmp.status.simulator.generated.StatusType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.util.List;

/**
 * Class that defines the configuration of the status simulator
 */
public class SimulatorConfiguration {
    private final SimulatedStatusesType simulatedStatuses;

    public SimulatorConfiguration(InputStream resourceAsStream) throws JAXBException {
        ClassLoader cl = ObjectFactory.class.getClassLoader();
        JAXBContext jaxbContext = JAXBContext.
                newInstance(ObjectFactory.class.getPackage().getName(), cl);
        Unmarshaller u = jaxbContext.createUnmarshaller();
        simulatedStatuses = u.unmarshal(new StreamSource(resourceAsStream), SimulatedStatusesType.class).getValue();
    }

    public List<StatusType> getStatuses() {
        return simulatedStatuses.getStatus();
    }
}
