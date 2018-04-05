package edu.gemini.aspen.gmp.services.core;

/**
 * The Service Processor interface. 
 */
public interface ServiceProcessor {

    /**
     * Register services to handle specific requests
     *
     * @param service A service to register
     */
    void registerService(Service service);


    /**
     * Process the given request using the given service type if it
     * is available
     * @param type The service type to use to process the request.
     * If no service is available, the request is ignored
     * @param request Request to be processed.
     * @throws ServiceException in case a problem happens while processing
     * the request by the service
     */
    void process(ServiceType type, ServiceRequest request) throws ServiceException;

}
