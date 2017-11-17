package org.cthul.fixsure.generators.composite;

import java.util.HashSet;
import java.util.Set;
import org.cthul.fixsure.DataSource;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.GeneratorException;
import org.cthul.fixsure.generators.CopyableGenerator;
import org.cthul.fixsure.generators.GeneratorTools;
import static org.cthul.fixsure.generators.GeneratorTools.copyGenerator;

/**
 * Uses a HashSet to ensure values are not returned twice.
 */
public class DistinctGenerator<T> implements CopyableGenerator<T> {
    
    public static <T> DistinctGenerator<T> distinct(DataSource<T> source) {
        return new DistinctGenerator<>(source);
    }
    
    public static <T> DistinctGenerator<T> distinct(DataSource<T> source, int maxAttempts) {
        return new DistinctGenerator<>(source, maxAttempts);
    }
    
    private static final int DEFAULT_MAX_ATTEMPTS = 128;
    
    private final int maxAttempts;
    private final Generator<T> source;
    private final Set<T> oldValues;

    public DistinctGenerator(DataSource<T> source) {
        this(source, DEFAULT_MAX_ATTEMPTS);
    }
    
    public DistinctGenerator(DataSource<T> source, int maxAttempts) {
        this.source = source.toGenerator();
        this.oldValues = new HashSet<>();
        this.maxAttempts = maxAttempts;
    }

    protected DistinctGenerator(DistinctGenerator<T> src) {
        this.source = copyGenerator(src.source);
        this.oldValues = new HashSet<>(src.oldValues);
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
    public DistinctGenerator<T> copy() {
        return new DistinctGenerator<>(this);
    }

    @Override
    public Class<T> getValueType() {
        return GeneratorTools.typeOf(source);
    }
}
