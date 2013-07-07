package org.cthul.fixsure.base;

import org.cthul.fixsure.GeneratorException;
import org.cthul.fixsure.Sequence;
import org.cthul.fixsure.fluents.FlSequence;
import org.cthul.fixsure.generators.composite.RandomizedGenerator;
import org.cthul.fixsure.generators.composite.RoundRobinSequence;
import org.cthul.fixsure.generators.composite.ShufflingGenerator;

/**
 *
 */
public abstract class SequenceBase<T> 
                extends GeneratorBase<T> 
                implements FlSequence<T> {

    private long index = 0;

    public SequenceBase() {
    }
    
    public SequenceBase(SequenceBase<?> src) {
        this.index = src.index;
    }

    @Override
    protected Sequence<T> publishThis() {
        return (Sequence) super.publishThis();
    }
    
    @Override
    public T next() {
        if (index >= length()) {
            throw new GeneratorException("End of sequence");
        }
        return value(index++);
    }

    @Override
    public ShufflingGenerator<T> shuffle() {
        return ShufflingGenerator.shuffle(this);
    }
    
    @Override
    public RandomizedGenerator<T> random() {
        return RandomizedGenerator.random(this);
    }

    @Override
    public <T2> RoundRobinSequence<T2> alternateWith(Sequence<? extends T2>... sequences) {
        return RoundRobinSequence.alternate(thisWith(sequences));
    }
    
    protected Sequence[] thisWith(Sequence[] sequences) {
        Sequence[] all = new Sequence[sequences.length+1];
        all[0] = publishThis();
        System.arraycopy(sequences, 0, all, 1, sequences.length);
        return all;
    }
    
}
