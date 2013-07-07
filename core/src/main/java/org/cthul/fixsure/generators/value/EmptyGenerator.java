package org.cthul.fixsure.generators.value;

import org.cthul.fixsure.GeneratorException;
import org.cthul.fixsure.base.SequenceBase;
import org.cthul.fixsure.fluents.FlGenerator;
import org.cthul.fixsure.fluents.FlGeneratorTemplate;
import org.hamcrest.Factory;

/**
 *
 */
public class EmptyGenerator<T> 
                extends SequenceBase<T>
                implements FlGeneratorTemplate<T> {

    private static final EmptyGenerator INSTANCE = new EmptyGenerator();
    
    @Factory
    public static <T> EmptyGenerator<T> noValues() {
        return INSTANCE;
    }
    
    @Override
    public T next() {
        throw new GeneratorException();
    }

    @Override
    public T value(long n) {
        throw new GeneratorException("No items");
    }

    @Override
    public long length() {
        return 0;
    }

    @Override
    public FlGenerator<T> newGenerator() {
        return this;
    }
    
}
