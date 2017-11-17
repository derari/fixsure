package org.cthul.fixsure.generators;

import org.cthul.fixsure.fluents.FlSequence;

/**
 *
 */
public abstract class BoundedSequence<T> implements FlSequence<T> {

    @Override
    public boolean isUnbounded() {
        return false;
    }

    @Override
    public boolean negativeIndices() {
        return false;
    }
}
