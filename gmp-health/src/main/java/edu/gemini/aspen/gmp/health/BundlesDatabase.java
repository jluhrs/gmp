package edu.gemini.aspen.gmp.health;

import com.google.common.util.concurrent.AtomicDouble;

/**
 * Created with IntelliJ IDEA.
 * User: cquiroz
 * Date: 10/22/12
 * Time: 10:07 AM
 * To change this template use File | Settings | File Templates.
 */
public interface BundlesDatabase {
    AtomicDouble getPercentageActive();
}
