package edu.gemini.aspen.gmp.health;

import com.google.common.util.concurrent.AtomicDouble;

public interface BundlesDatabase {
    AtomicDouble getPercentageActive();
}
