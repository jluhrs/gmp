package edu.gemini.aspen.gds.postprocessingpolicy

import edu.gemini.aspen.gds.api.{PostProcessingPolicy, DefaultPostProcessingPolicy}
import org.apache.felix.ipojo.annotations.{Property, Provides, Component}
import java.io.File
import sys.process._

/**
 * Post Processing Policy to set the owner on the file to fit archiving requirements
 */
@Component
@Provides(specifications = Array[Class[_]](classOf[PostProcessingPolicy]))
class SetOwnershipPolicy(@Property (name = "owner", value = "gpi", mandatory = true) owner: String, @Property (name = "useSudo", value = "true", mandatory = true) sudo: String) extends DefaultPostProcessingPolicy {
  val useSudo = sudo.equalsIgnoreCase("true")

  override val priority = 13

  override def fileReady(originalFile: File, processedFile: File) {
    LOG.info(s"Set file $processedFile ownership to $owner")

    val cmd = s"${if (useSudo) "sudo " else ""}chown $owner $processedFile"
    LOG.info(cmd)
    
    val result = cmd.!
    if (result != 0) {
      LOG.severe(s"Failed command $cmd")
    }
  }
}
