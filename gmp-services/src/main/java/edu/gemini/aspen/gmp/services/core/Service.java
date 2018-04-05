package edu.gemini.aspen.gmp.services.core;

/**
 * A Service definition interface.
 *
 * All services will have a method to process a given request, and a
 * type. 
 *
 */
public interface Service {

    /**
     * Process the given request
     * @param request Contains the details about the specific request
     * @throws ServiceException in case the service can not process the
     * request
     */
    void process(ServiceRequest request) throws ServiceException;

    /**
     * The service type
     * @return enumerated type to describe the service
     */
    ServiceType getType();

}
