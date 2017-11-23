package org.cthul.fixsure;

import org.cthul.fixsure.fluents.FlDataSource;
import org.cthul.fixsure.fluents.FlTemplate;

/**
 * A source of data, either a {@link Generator} or a {@link Template}.
 * @param <T> value type
 */
@FunctionalInterface
public interface DataSource<T> {
    
    /**
     * Returns a generator for this data.
     * If this is a template, returns a new generator;
     * if this is a generator, returns itself.
     * @return generator
     */
    Generator<T> toGenerator();
    
    /**
     * Provides access to the {@linkplain FlDataSource fluent data source} interface.
     * @return fluent
     */
    default FlDataSource<T> fluentData() {
        return (FlTemplate<T>) () -> toGenerator().fluentData();
    }
    
    /**
     * Converts an array of data sources into generators by calling
     * {@link #toGenerator()} on each.
     * @param <T>
     * @param sources
     * @return generators
     */
    static <T> Generator<T>[] toGenerators(DataSource<T>... sources) {
        Generator<T>[] result = new Generator[sources.length];
        for (int i = 0; i < sources.length; i++) {
            result[i] = sources[i].toGenerator();
        }
        return result;
    }

    /**
     * Converts data sources into generators by calling
     * {@link #toGenerator()} on each.
     * @param <T>
     * @param first
     * @param more
     * @return generators
     */
    static <T> Generator<T>[] toGenerators(DataSource<T> first, DataSource<T>... more) {
        Generator<T>[] result = new Generator[more.length+1];
        result[0] = first.toGenerator();
        for (int i = 0; i < more.length; i++) {
            result[i+1] = more[i].toGenerator();
        }
        return result;
    }
}
