package org.cthul.fixsure.generators;

import org.cthul.fixsure.api.AbstractStringify;
import org.cthul.fixsure.distributions.DistributionRandomizer;
import org.cthul.fixsure.fluents.FlSequence;

/**
 *
 */
public abstract class BoundedSequence<T> extends AbstractStringify implements FlSequence<T> {

    @Override
    public boolean isUnbounded() {
        return false;
    }

    @Override
    public boolean negativeIndices() {
        return false;
    }

    @Override
    public long randomSeedHint() {
        return DistributionRandomizer.toSeed(getClass()) ^ length();
    }
}
