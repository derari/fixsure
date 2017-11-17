package org.cthul.fixsure;

/**
 * 
 */
public interface SequenceLength {
    
    /**
     * Number of elements that can be accessed through {@link #value(long)};
     * -1 if any positive long is accepted.
     * @return length
     */
    long length();
    
    default boolean isUnbounded() {
        return length() <= L_UNBOUNDED;
    }
    
    /**
     * Only if unbounded.
     * @return 
     */
    default boolean negativeIndices() {
        return length() <= L_NEGATIVE_INDICES;
    }

    static boolean isInRange(long index, SequenceLength length) {
        if (index < 0) {
            return length.negativeIndices();
        }
        if (length.isUnbounded()) {
            return true;
        }
        return index < length.length();
    }
    
    static long toLong(SequenceLength length) {
        if (length.isUnbounded()) {
            return length.negativeIndices() ? L_NEGATIVE_INDICES : L_UNBOUNDED;
        }
        return length.length();
    }
    
    static SequenceLength min(SequenceLength a, SequenceLength b) {
        if (a.isUnbounded()) {
            if (!b.isUnbounded()) return b;
            if (a.negativeIndices() && !b.negativeIndices()) {
                return b;
            }
            return a;
        }
        if (b.isUnbounded()) return a;
        return a.length() < b.length() ? a : b;
    }
    
    static final long L_UNBOUNDED = -1;
    static final long L_NEGATIVE_INDICES = -2;
    
    static final SequenceLength UNBOUNDED = () -> L_UNBOUNDED;
    static final SequenceLength NEGATIVE_INDICES = () -> L_NEGATIVE_INDICES;
}
