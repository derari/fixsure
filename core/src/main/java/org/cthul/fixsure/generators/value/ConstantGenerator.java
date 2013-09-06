package org.cthul.fixsure.generators.value;

import org.cthul.fixsure.base.SequenceBase;
import org.cthul.fixsure.fluents.FlGeneratorTemplate;
import org.hamcrest.Factory;

/**
 *
 */
public class ConstantGenerator<T> 
                extends SequenceBase<T>
                implements FlGeneratorTemplate<T> {
    
    private static final ConstantGenerator NULLS = new ConstantGenerator(null);
    
    @Factory
    public static <T> ConstantGenerator<T> constant(T value) {
        return new ConstantGenerator<>(value);
    }
    
    @Factory
    public static <T> ConstantGenerator<T> nullValues() {
        return NULLS;
    }
    
    private final T value;

    public ConstantGenerator(T value) {
        this.value = value;
    }

    @Override
    public Class<T> getValueType() {
        if (value == null) {
            return null;
        }
        return (Class) value.getClass();
    }

    @Override
    public T next() {
        return value;
    }

    @Override
    public T value(long n) {
        return value;
    }

    @Override
    public long length() {
        return -1;
    }

    @Override
    public ConstantGenerator<T> newGenerator() {
        return this;
    }

}
