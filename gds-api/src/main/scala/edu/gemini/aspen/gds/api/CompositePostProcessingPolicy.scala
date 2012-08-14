package edu.gemini.aspen.gds.api

import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.giapi.data.DataLabel
import scala.collection._

/**
 * Interface for an PostProcessingPolicy that uses OSGi services implementing error policies
 */
trait CompositePostProcessingPolicy extends PostProcessingPolicy

/**
 * OSGi service implementing the CompositePostProcessingPolicy that can use delegates
 */
@Component
@Instantiate
@Provides(specifications = Array[Class[_]](classOf[CompositePostProcessingPolicy]))
class CompositePostProcessingPolicyImpl extends DefaultPostProcessingPolicy with CompositePostProcessingPolicy {
  @volatile var policies = immutable.List[PostProcessingPolicy]()

  // Apply all the original headers
  override def applyPolicy(dataLabel: DataLabel, headers: immutable.List[CollectedValue[_]]): immutable.List[CollectedValue[_]] = {
    LOG.info("Applying policies: " + policies.sortWith((a, b) => a.priority < b.priority).foldLeft("")((B, A) => B + " " + A.getClass.getSimpleName))
    //iterative
    //        var h = headers
    //        for (ep <- policies) {
    //            h = ep.applyPolicy(dataLabel, h)
    //        }
    //        h

    //recursive
    applyPolicies(dataLabel, policies.sortWith((a, b) => a.priority < b.priority), headers)
  }

  private def applyPolicies(dataLabel: DataLabel, l: immutable.List[PostProcessingPolicy], headers: immutable.List[CollectedValue[_]]): immutable.List[CollectedValue[_]] = {
    l match {
      case Nil => headers
      case _ => l.last.applyPolicy(dataLabel, applyPolicies(dataLabel, l.init, headers))
    }

  }

  @Bind(optional = true, aggregate = true)
  def bindPolicy(ep: PostProcessingPolicy) {
    policies = ep :: policies
  }

  @Validate
  def validate() {
    // Required for iPojo
  }
}