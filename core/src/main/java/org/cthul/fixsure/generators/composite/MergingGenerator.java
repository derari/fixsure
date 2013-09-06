package org.cthul.fixsure.generators.composite;

import org.cthul.fixsure.Generator;
import org.cthul.fixsure.GeneratorException;
import org.cthul.fixsure.base.GeneratorBase;
import org.cthul.fixsure.base.GeneratorTools;
import org.cthul.fixsure.fluents.FlGeneratorTemplate;
import org.hamcrest.Factory;

/**
 * Merges multiple generators that produce sorted values into one.
 */
public class MergingGenerator<T>
                extends GeneratorBase<T>
                implements FlGeneratorTemplate<T> {
    
    private static final Object NO_VALUE = new Object();
    
    @Factory
    public static <T> MergingGenerator<T> merge(Generator<? extends T>... generators) {
        return new MergingGenerator<>(generators);
    }
    
    private final Generator<? extends T>[] generators;
    private final Object[] nextValues;
    private Class<?> valueType = void.class;

    public MergingGenerator(Generator<? extends T>[] generators) {
        this.generators = generators;
        this.nextValues = new Object[generators.length];
        for (int i = 0; i < nextValues.length; i++) {
            nextValue(i);
        }
    }

    public MergingGenerator(Class<T> valueType, Generator<? extends T>[] generators) {
        this(generators);
        this.valueType = valueType;
    }

    public MergingGenerator(MergingGenerator<T> src) {
        this.generators = src.generators.clone();
        for (int i = 0; i < this.generators.length; i++) {
            this.generators[i] = GeneratorTools.newGeneratorFromTemplate(this.generators[i]);
        }
        this.valueType = src.valueType;
        this.nextValues = src.nextValues;
    }
    
    private void nextValue(int i) {
        try {
            nextValues[i] = generators[i].next();
        } catch (GeneratorException e) {
            nextValues[i] = NO_VALUE;
        }
    }
    
    @Override
    public T next() {
        int index = -1;
        Object value = null;
        for (int i = 0; i < nextValues.length; i++) {
            Object valueI = nextValues[i];
            if (valueI != NO_VALUE) {
                if (index < 0 || lessThan(valueI, value)) {
                    value = valueI;
                    index = i;
                }
            }
        }
        if (index < 0) {
            throw new GeneratorException("No more items");
        }
        nextValue(index);
        return (T) value;
    }
    
    private boolean lessThan(Object o1, Object o2) {
        Comparable c1 = (Comparable) o1;
        Comparable c2 = (Comparable) o2;
        return c1.compareTo(o2) < 0;
    }

    @Override
    public Class<T> getValueType() {
        if (valueType == void.class) {
            valueType = GeneratorTools.commonTypeOf((Object[]) generators);
        }
        return (Class) valueType;
    }

    @Override
    public MergingGenerator<T> newGenerator() {
        return new MergingGenerator<>(this);
    }
    
}
