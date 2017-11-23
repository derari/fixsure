package org.cthul.fixsure;

import org.cthul.fixsure.fetchers.Fetchers;
import org.cthul.fixsure.values.EagerValues;
import org.cthul.fixsure.values.LazyValues;
import org.cthul.fixsure.fluents.FlCardinality;

/**
 * Describes how and how many values should be fetched from a {@link Generator}.
 * <p>
 * Default implementations can be obtained from the {@link Fetchers} class.
 * @see Fetchers
 * @see LazyValues
 * @see EagerValues
 */
@FunctionalInterface
public interface Cardinality extends DataSource<Integer>, Typed<Integer> {

    /**
     * Returns a {@link Fetcher} for this cardinality.
     * @return fetcher
     */
    Fetcher toFetcher();
    
    /**
     * Provides access to the {@linkplain FlCardinality fluent cardinality} interface.
     * @return fluent
     */
    default FlCardinality fluentCardinality() {
        return () -> toFetcher().fluentCardinality();
    }

    /**
     * Type is always integer.
     * @return {@code Integer.class}
     */
    @Override
    default Class<Integer> getValueType() {
        return Integer.class;
    }

    /**
     * Alias for {@link #toFetcher()}.
     * @return fetcher
     */
    @Override
    default Generator<Integer> toGenerator() {
        return toFetcher();
    }

    /**
     * Retrieves elements from a generator. Fetching may occur eager or lazy.
     * <p>
     * The integer generated is the number of elements that 
     * would have been fetched and may be different for each call.
     */
    @FunctionalInterface
    public static interface Fetcher extends Cardinality, Generator<Integer> {

        /**
         * The next number of elements that would have been fetched.
         * @return length
         */
        int nextLength();

        /**
         * Fetches elements from {@code generator}.
         * @param <T>
         * @param generator
         * @return values
         */
        default<T> Values<T> of(DataSource<T> generator) {
            // as a functional interface, this only produces integers
            // rely on existing implementations to implement ´of´
            return Fetchers.next(this).toFetcher().of(generator);
        }

        /**
         * Fetches elements from each generator.
         * @param <T>
         * @param generators
         * @return values
         */
        default<T> Values<T> ofEach(DataSource<? extends T>... generators) {
            // as a functional interface, this only produces integers
            // rely on existing implementations to implement ´ofEach´
            return Fetchers.next(this).toFetcher().<T>ofEach(generators);
        }

        /**
         * Alias for {@link #nextLength()}.
         * @return length
         */
        @Override
        default Integer next() {
            return nextLength();
        }

        /**
         * Returns self.
         * @return this
         * @deprecated redundant operation
         */
        @Override
        @Deprecated
        default Fetcher toFetcher() {
            return this;
        }

        /**
         * Returns self.
         * @return this
         * @deprecated Redundant operation
         */
        @Override
        @Deprecated
        default Generator<Integer> toGenerator() {
            return this;
        }

        /**
         * Provides access to the {@linkplain FlCardinality.FlFetcher fluent fetcher} interface.
         * @return fluent
         */
        @Override
        default FlCardinality.FlFetcher fluentCardinality() {
            return FlCardinality.wrap(this);
        }
    }
}
