package org.cthul.fixsure.generators.primitives;

import org.cthul.fixsure.Factory;
import org.cthul.fixsure.GeneratorException;
import org.cthul.fixsure.generators.BoundedSequence;

/**
 * Generates consecutive integers.
 */
public class ConsecutiveIntegersSequence extends BoundedSequence<Integer> {
    
    /**
     * Generates consecutive integers.
     * @return consecutive integers
     */
    @Factory
    public static ConsecutiveIntegersSequence consecutiveIntegers() {
        return new ConsecutiveIntegersSequence();
    }

    /**
     * Generates consecutive integers.
     * @param first
     * @return consecutive integers
     */
    @Factory
    public static ConsecutiveIntegersSequence consecutiveIntegers(int first) {
        return new ConsecutiveIntegersSequence(first);
    }

    /**
     * Generates consecutive integers.
     * @param first
     * @param step
     * @return consecutive integers
     */
    @Factory
    public static ConsecutiveIntegersSequence consecutiveIntegers(int first, int step) {
        return new ConsecutiveIntegersSequence(first, step);
    }

    /**
     * Generates consecutive integers.
     * @param first
     * @param step
     * @param end
     * @return consecutive integers
     */
    @Factory
    public static ConsecutiveIntegersSequence consecutiveIntegers(int first, int step, int end) {
        return new ConsecutiveIntegersSequence(first, step, end);
    }

    private final int first, step, end;

    public ConsecutiveIntegersSequence() {
        this(0, 1, Integer.MAX_VALUE);
    }
    
    public ConsecutiveIntegersSequence(int first) {
        this(first, 1, Integer.MAX_VALUE);
    }
    
    public ConsecutiveIntegersSequence(int first, int step) {
        this(first, step, Integer.MAX_VALUE);
    }
    
    public ConsecutiveIntegersSequence(int first, int step, int end) {
        this.first = first;
        this.step = step;
        this.end = end;
    }

    public ConsecutiveIntegersSequence(ConsecutiveIntegersSequence src) {
        this.first = src.first;
        this.step = src.step;
        this.end = src.end;
    }
    
    @Override
    public long length() {
        return (end - first) / step;
    }

    @Override
    public Integer value(long n) {
        int v = first + ((int) n)*step;
        if (step > 0) {
            if (v < first || v > end) {
                throw new GeneratorException();
            }
        } else if (step < 0) {
            if (v > first || v < end) {
                throw new GeneratorException();
            }
        }
        return v;
    }
}
