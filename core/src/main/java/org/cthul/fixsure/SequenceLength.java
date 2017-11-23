package org.cthul.fixsure;

/**
 * Describes the length of a {@link Sequence}.
 */
public interface SequenceLength {
    
    /**
     * Number of elements that can be accessed through {@link #value(long)};
     * negative if sequence is unbounded or allows negative indices.
     * @return length
     */
    long length();
    
    /**
     * Indicates whether every positive long is accepted.
     * @return true if unbounded
     */
    default boolean isUnbounded() {
        return length() <= L_UNBOUNDED;
    }
    
    /**
     * Indicates whether negative indices are accepted.
     * Only if unbounded.
     * @return true if negative indices are supported.
     */
    default boolean negativeIndices() {
        return length() <= L_NEGATIVE_INDICES;
    }

    /**
     * Checks whether an index is in range.
     * @param index
     * @param length
     * @return true if index is in range
     */
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
