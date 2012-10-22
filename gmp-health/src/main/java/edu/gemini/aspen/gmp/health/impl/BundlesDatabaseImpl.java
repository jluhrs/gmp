package edu.gemini.aspen.gmp.health.impl;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.AtomicDouble;
import com.sun.istack.internal.Nullable;
import edu.gemini.aspen.gmp.health.BundlesDatabase;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

import java.util.Collection;

@Component
@Instantiate
public class BundlesDatabaseImpl implements BundlesDatabase {
    private final BundleContext context;
    private final AtomicDouble percentageActive = new AtomicDouble(1);

    public BundlesDatabaseImpl(BundleContext context) {
        this.context = context;
        context.addBundleListener(new BundleListener() {
            @Override
            public void bundleChanged(BundleEvent bundleEvent) {
                System.out.println("ev " + bundleEvent);
            }
        });
        calculateActiveBundles();
    }

    private void calculateActiveBundles() {
        ImmutableList<Bundle> bundles = ImmutableList.copyOf(this.context.getBundles());
        Collection<Boolean> activeBooleans = Collections2.transform(bundles, new Function<Bundle, Boolean>() {
            @Override
            public Boolean apply(@Nullable Bundle bundle) {
                return bundle.getState() == Bundle.ACTIVE;
            }
        });
        percentageActive.set(((double)activeBooleans.size())/bundles.size());
    }

    @Override
    public AtomicDouble getPercentageActive() {
        return percentageActive;
    }
}
