package org.cthul.fixsure.generators.composite;

import java.util.function.Predicate;
import org.cthul.fixsure.DataSource;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.generators.GeneratorTools;
import org.cthul.fixsure.generators.CopyableGenerator;
import static org.cthul.fixsure.generators.GeneratorTools.copyGenerator;

/**
 * Uses a HashSet to ensure values are not returned twice.
 */
public class FilteringGenerator<T> implements CopyableGenerator<T> {
   
    public static <T> FilteringGenerator<T> filter(DataSource<T> source, Predicate<? super T> predicate) {
        return new FilteringGenerator<>(source, predicate);
    }
    
    private final Generator<T> source;
    private final Predicate<? super T> predicate;

    public FilteringGenerator(DataSource<T> source, Predicate<? super T> predicate) {
        this.source = source.toGenerator();
        this.predicate = predicate;
    }

    protected FilteringGenerator(FilteringGenerator<T> src) {
        this.source = copyGenerator(src.source);
        this.predicate = src.predicate;
    }
    
    @Override
    public T next() {
        while (true) {
            T next = source.next();
            if (predicate.test(next)) {
                return next;
            }
        }
    }

    @Override
    public FilteringGenerator<T> copy() {
        return new FilteringGenerator<>(this);
    }

    @Override
    public Class<T> getValueType() {
        return GeneratorTools.typeOf(source);
    }
}
