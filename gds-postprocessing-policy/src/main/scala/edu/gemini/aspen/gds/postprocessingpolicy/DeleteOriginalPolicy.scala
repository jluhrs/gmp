package edu.gemini.aspen.gds.postprocessingpolicy

import edu.gemini.aspen.gds.api.{PostProcessingPolicy, DefaultPostProcessingPolicy}
import org.apache.felix.ipojo.annotations.{Provides, Component}
import java.io.File
import com.google.common.io.Files

/**
 * This policy removes ErrorCollectedValues. It should probably be the last policy applied.
 */
@Component
@Provides(specifications = Array[Class[_]](classOf[PostProcessingPolicy]))
class DeleteOriginalPolicy extends DefaultPostProcessingPolicy {

  override val priority = 12

  override def toString = this.getClass.getSimpleName

  override def fileReady(originalFile: File, processedFile: File) {
    LOG.info(s"Delete original file $originalFile")
    if (!originalFile.delete()) {
      LOG.warning(s"Could not delete original file $originalFile")
    }
  }
}