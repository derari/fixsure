package org.cthul.fixsure.generators.composite;

import org.cthul.fixsure.Generator;
import org.cthul.fixsure.Sequence;
import org.cthul.fixsure.base.GeneratorTools;
import org.cthul.fixsure.base.SequenceBase;
import org.cthul.fixsure.fluents.FlGeneratorTemplate;
import org.hamcrest.Factory;

/**
 *
 */
public class RoundRobinSequence<T>
                extends SequenceBase<T> 
                implements FlGeneratorTemplate<T> {
    
    @Factory
    public static <T> RoundRobinSequence<T> rotate(Sequence<? extends T>... generators) {
        return new RoundRobinSequence<>(generators);
    }
    
    @Factory
    public static <T> RoundRobinSequence<T> alternate(Sequence<? extends T>... generators) {
        return new RoundRobinSequence<>(generators);
    }
    
    private final Sequence<? extends T>[] generators;
    private long length = -2;
    private Class<?> valueType = void.class;
    private int n = 0;

    public RoundRobinSequence(Sequence<? extends T>[] generators) {
        this.generators = generators;
    }

    public RoundRobinSequence(Class<T> valueType, Sequence<? extends T>[] generators) {
        this(generators);
        this.valueType = valueType;
    }

    public RoundRobinSequence(RoundRobinSequence<T> src) {
        this.generators = src.generators.clone();
        for (int i = 0; i < this.generators.length; i++) {
            this.generators[i] = (Sequence) GeneratorTools.newGeneratorFromTemplate(this.generators[i]);
        }
        this.length = src.length;
        this.valueType = src.valueType;
        this.n = src.n;
    }

    @Override
    public T next() {
        return generators[(n++) % generators.length].next();
    }

    @Override
    public Class<T> getValueType() {
        if (valueType == void.class) {
            valueType = GeneratorTools.commonTypeOf((Object[]) generators);
        }
        return (Class) valueType;
    }

    @Override
    public T value(long n) {
        int g = (int) (n % generators.length);
        long i = n / generators.length;
        return generators[g].value(i);
    }

    @Override
    public long length() {
        if (length == -2) {
            long l = Long.MAX_VALUE;
            for (Sequence<?> s: generators) {
                long l2 = s.length();
                if (l2 >= 0) l = Math.min(l, l2);
            }
            l *= generators.length;
            if (l < 0) l = -1;
            length = l;
        }
        return length;
    }

    @Override
    public RoundRobinSequence<T> newGenerator() {
        return new RoundRobinSequence<>(this);
    }
    
}
