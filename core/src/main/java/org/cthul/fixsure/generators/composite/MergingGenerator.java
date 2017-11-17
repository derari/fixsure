package org.cthul.fixsure.generators.composite;

import org.cthul.fixsure.DataSource;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.GeneratorException;
import org.cthul.fixsure.generators.GeneratorTools;
import org.cthul.fixsure.generators.CopyableGenerator;
import static org.cthul.fixsure.generators.GeneratorTools.copyGenerator;

/**
 * Merges multiple generators that produce sorted values into one.
 */
public class MergingGenerator<T> implements CopyableGenerator<T> {
    
    private static final Object NO_VALUE = new Object();
    
    public static <T> MergingGenerator<T> merge(DataSource<? extends T>... generators) {
        return new MergingGenerator<T>(generators);
    }
    
    private final Generator<? extends T>[] generators;
    private final Object[] nextValues;
    private Class<?> valueType = void.class;

    public MergingGenerator(DataSource<? extends T> first, DataSource<? extends T>[] more) {
        this(DataSource.toGenerators((DataSource) first, (DataSource[]) more));
    }

    public MergingGenerator(DataSource<? extends T>[] generators) {
        this(DataSource.toGenerators((DataSource[]) generators));
    }
    
    public MergingGenerator(Generator<? extends T>[] generators) {
        this.generators = generators;
        this.nextValues = new Object[generators.length];
        for (int i = 0; i < nextValues.length; i++) {
            nextValue(i);
        }
    }

    public MergingGenerator(Class<T> valueType, DataSource<? extends T>[] generators) {
        this(generators);
        this.valueType = valueType;
    }

    public MergingGenerator(MergingGenerator<T> src) {
        this.generators = src.generators.clone();
        for (int i = 0; i < this.generators.length; i++) {
            this.generators[i] = copyGenerator(this.generators[i]);
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
        return c1.compareTo(c2) < 0;
    }

    @Override
    public Class<T> getValueType() {
        if (valueType == void.class) {
            valueType = GeneratorTools.commonTypeOf((Object[]) generators);
        }
        return (Class) valueType;
    }

    @Override
    public MergingGenerator<T> copy() {
        return new MergingGenerator<>(this);
    }    
}
