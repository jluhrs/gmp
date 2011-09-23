package edu.gemini.aspen.gds.api

import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.giapi.data.DataLabel

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
class CompositeErrorPolicyImpl extends DefaultErrorPolicy with CompositeErrorPolicy {
  var policies: List[ErrorPolicy] = List()

  // Apply all the original headers
  override def applyPolicy(dataLabel: DataLabel, headers: List[CollectedValue[_]]): List[CollectedValue[_]] = {
    LOG.info("Applying policies: " + policies.foldLeft("")((B, A) => B + " " + A.getClass.getCanonicalName))
    //iterative
    //        var h = headers
    //        for (ep <- policies) {
    //            h = ep.applyPolicy(dataLabel, h)
    //        }
    //        h

    //recursive
    applyPolicies(dataLabel, policies.sortWith((a, b) => a.priority < b.priority), headers)
  }

  private def applyPolicies(dataLabel: DataLabel, l: List[ErrorPolicy], headers: List[CollectedValue[_]]): List[CollectedValue[_]] = {
    l match {
      case Nil => headers
      case _ => l.last.applyPolicy(dataLabel, applyPolicies(dataLabel, l.init, headers))
    }

  }

  @Bind(optional = true, aggregate = true)
  def bindPolicy(ep: ErrorPolicy) {
    policies = ep :: policies
  }

  @Validate
  def validate() {
  }
}