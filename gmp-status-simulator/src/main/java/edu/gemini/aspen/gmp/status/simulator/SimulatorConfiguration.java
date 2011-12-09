package edu.gemini.aspen.gmp.status.simulator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

/**
 * Class that defines the configuration of the status simulator
 */
public class SimulatorConfiguration {
    public SimulatorConfiguration(InputStream resourceAsStream) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.
                newInstance("edu.gemini.aspen.gmp.status.simulator.generated");
        Unmarshaller u = jaxbContext.createUnmarshaller();
        System.out.println(u.unmarshal(resourceAsStream));
    }
}
