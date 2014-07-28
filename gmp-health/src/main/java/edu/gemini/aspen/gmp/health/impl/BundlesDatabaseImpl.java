package edu.gemini.aspen.gmp.health.impl;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.AtomicDouble;
import edu.gemini.aspen.gmp.health.BundlesDatabase;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

public class BundlesDatabaseImpl implements BundlesDatabase {
    private final BundleContext context;
    private final AtomicDouble percentageActive = new AtomicDouble(1);

    public BundlesDatabaseImpl(BundleContext context) {
        this.context = context;
        context.addBundleListener(new BundleListener() {
            @Override
            public void bundleChanged(BundleEvent bundleEvent) {
                calculateActiveBundles();
            }
        });
        calculateActiveBundles();
    }

    private void calculateActiveBundles() {
        ImmutableList<Bundle> bundles = ImmutableList.copyOf(this.context.getBundles());
        ImmutableList<Bundle> activeBundles = ImmutableList.copyOf(Collections2.filter(bundles, new Predicate<Bundle>() {
            @Override
            public boolean apply(Bundle bundle) {
                return bundle.getState() == Bundle.ACTIVE || bundle.getHeaders().get("Fragment-Host") != null;
            }
        }));
        percentageActive.set(((double) activeBundles.size()) / bundles.size());
    }

    @Override
    public AtomicDouble getPercentageActive() {
        return percentageActive;
    }
}
