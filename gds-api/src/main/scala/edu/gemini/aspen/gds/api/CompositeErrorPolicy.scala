package edu.gemini.aspen.gds.api

import org.apache.felix.ipojo.annotations.{Provides, Instantiate, Component}

/**
 * Interface for an ErrorPolicy that uses OSGi services implementing error policies
 */
trait CompositeErrorPolicy extends ErrorPolicy

/**
 * OSGi service implementing the CompositeErrorPolicy that can use delegates
 */
@Component
@Instantiate
@Provides(specifications = Array(classOf[CompositeErrorPolicy]))
class CompositeErrorPolicyImpl extends DefaultErrorPolicy with CompositeErrorPolicy