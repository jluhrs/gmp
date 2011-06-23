package edu.gemini.aspen.gds.errorpolicy

import org.apache.felix.ipojo.annotations.{Provides, Component}
import edu.gemini.aspen.gds.api.{DefaultErrorPolicy, ErrorPolicy}

@Component
@Provides
class ErrorsRemovedPolicy extends DefaultErrorPolicy