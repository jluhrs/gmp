package edu.gemini.aspen.gmp.services.core;

import org.junit.Test;

import static org.mockito.Mockito.*;

public class ServiceProcessorImplTest {
    @Test
    public void testProcessUnknownType() throws ServiceException {
        ServiceProcessor serviceProcessor = new ServiceProcessorImpl();
        // Basically we test that no exceptions are thrown
        serviceProcessor.process(ServiceType.PROPERTY_SERVICE, new ServiceRequest() {
        });
    }

    @Test
    public void testProcessKnownType() throws ServiceException {
        ServiceProcessor serviceProcessor = new ServiceProcessorImpl();
        Service service = mock(Service.class);
        when(service.getType()).thenReturn(ServiceType.PROPERTY_SERVICE);
        serviceProcessor.registerService(service);
        // Basically we test that no exceptions are thrown
        ServiceRequest request = new ServiceRequest() {};
        serviceProcessor.process(ServiceType.PROPERTY_SERVICE, request);

        verify(service).process(request);
    }
}
