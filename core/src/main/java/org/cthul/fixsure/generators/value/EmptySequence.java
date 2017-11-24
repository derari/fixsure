package org.cthul.fixsure.generators.value;

import org.cthul.fixsure.api.Factory;
import org.cthul.fixsure.generators.BoundedSequence;

/**
 *
 */
public class EmptySequence<T> extends BoundedSequence<T> {

    private static final EmptySequence INSTANCE = new EmptySequence();
    
    /**
     * Returns a sequence with length zero.
     * @param <T>
     * @return empty sequence
     */
    @Factory
    public static <T> EmptySequence<T> noValues() {
        return INSTANCE;
    }

    @Override
    public T value(long n) {
        throw new IndexOutOfBoundsException("" + n);
    }

    @Override
    public long length() {
        return 0;
    }
    
    @Override
    public Class<T> getValueType() {
        return null;
    }

    @Override
    public long randomSeedHint() {
        return 0;
    }

    @Override
    public StringBuilder toString(StringBuilder sb) {
        return sb.append("{}");
    }
}
