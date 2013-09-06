package org.cthul.fixsure.generators.primitive;

import org.cthul.fixsure.GeneratorException;
import org.cthul.fixsure.base.SequenceBase;
import org.cthul.fixsure.fluents.FlGeneratorTemplate;
import org.hamcrest.Factory;

/**
 * Generates consecutive integers.
 */
public class ConsecutiveIntegersSequence
                extends SequenceBase<Integer> 
                implements FlGeneratorTemplate<Integer> {
    
    @Factory
    public static ConsecutiveIntegersSequence consecutiveIntegers() {
        return new ConsecutiveIntegersSequence();
    }

    @Factory
    public static ConsecutiveIntegersSequence consecutiveIntegers(int first) {
        return new ConsecutiveIntegersSequence(first);
    }

    @Factory
    public static ConsecutiveIntegersSequence consecutiveIntegers(int first, int step) {
        return new ConsecutiveIntegersSequence(first, step);
    }

    @Factory
    public static ConsecutiveIntegersSequence consecutiveIntegers(int first, int step, int end) {
        return new ConsecutiveIntegersSequence(first, step, end);
    }

    private final int first, step, end;
    private int i; 

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
        this.i = 0;
    }

    public ConsecutiveIntegersSequence(ConsecutiveIntegersSequence src) {
        super(src);
        this.first = src.first;
        this.step = src.step;
        this.end = src.end;
        this.i = src.i;
    }
    
    @Override
    public long length() {
        return (end - first) / step;
    }

    @Override
    public Integer value(long n) {
        int v = first + (i++)*step;
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

    @Override
    public ConsecutiveIntegersSequence newGenerator() {
        return new ConsecutiveIntegersSequence(this);
    }
    
}
