package org.cthul.fixsure.generators.composite;

import java.util.HashSet;
import java.util.Set;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.GeneratorException;
import org.cthul.fixsure.base.GeneratorBase;
import org.cthul.fixsure.base.GeneratorTools;
import org.cthul.fixsure.fluents.FlGeneratorTemplate;
import org.hamcrest.Factory;

/**
 * Uses a HashSet to ensure values are not returned twice.
 */
public class UniqueGenerator<T>
                extends GeneratorBase<T> 
                implements FlGeneratorTemplate<T> {
    
    @Factory
    public static <T> UniqueGenerator<T> unique(Generator<T> source) {
        return new UniqueGenerator<>(source);
    }
    
    @Factory
    public static <T> UniqueGenerator<T> unique(Generator<T> source, int maxAttempts) {
        return new UniqueGenerator<>(source, maxAttempts);
    }
    
    private static final int DEFAULT_MAX_ATTEMPTS = 128;
    
    private final int maxAttempts;
    private final Generator<T> source;
    private final Set<T> oldValues;

    public UniqueGenerator(Generator<T> source) {
        this(source, DEFAULT_MAX_ATTEMPTS);
    }
    
    public UniqueGenerator(Generator<T> source, int maxAttempts) {
        this.source = source;
        this.oldValues = new HashSet<>();
        this.maxAttempts = maxAttempts;
    }

    protected UniqueGenerator(UniqueGenerator<T> src) {
        this.source = GeneratorTools.newGeneratorFromTemplate(src.source);
        this.oldValues = (Set<T>) ((HashSet) src.oldValues).clone();
        this.maxAttempts = src.maxAttempts;
    }
    
    @Override
    public T next() {
        T next = null;
        for (int i = 0; i < maxAttempts; i++) {
            next = source.next();
            if (oldValues.add(next)) {
                return next;
            }
        }
        throw new GeneratorException(
                "No unique value after " + maxAttempts + " attempts: " + next);
    }

    @Override
    public UniqueGenerator<T> newGenerator() {
        return new UniqueGenerator<>(this);
    }

    @Override
    public Class<T> getValueType() {
        return GeneratorTools.typeOf(source);
    }
    
}
