package org.cthul.fixsure;

import org.cthul.fixsure.fetchers.Fetchers;
import org.cthul.fixsure.fluents.FlDataSource;
import org.cthul.fixsure.fluents.FlFetcher;
import org.cthul.fixsure.fluents.FlFetcher.FlConsumer;
import org.cthul.fixsure.fluents.FlGenerator;
import org.cthul.fixsure.fluents.FlTemplate;
import org.cthul.fixsure.values.EagerValues;
import org.cthul.fixsure.values.LazyValues;

/**
 * Retrieves elements from a generator. Fetching may occur eager or lazy.
 * <p>
 * The integer generated is the number of elements that would have been fetched
 * (May be different for each call).
 * @see LazyValues
 * @see EagerValues
 */
@FunctionalInterface
public interface Fetcher extends DataSource<Integer>, Typed<Integer> {

    ItemConsumer toItemConsumer();
    
    default FlFetcher fluentFetcher() {
        return () -> toItemConsumer().fluentFetcher();
    }

    @Override
    default Class<Integer> getValueType() {
        return Integer.class;
    }

    @Override
    default Generator<Integer> toGenerator() {
        return toItemConsumer();
    }
    
    @FunctionalInterface
    interface ItemConsumer extends Fetcher, Generator<Integer> {

        /**
         * The next number of elements that would have been fetched.
         * @return length
         */
        int nextLength();

        /**
         * Fetches elements from {@code generator}
         * @param <T>
         * @param generator
         * @return values
         */
        default <T> Values<T> of(DataSource<T> generator) {
            return Fetchers.next(this).toItemConsumer().of(generator);
        }

        /**
         * Fetches elements from each generator.
         * @param <T>
         * @param generators
         * @return values
         */
        default <T> Values<T> ofEach(DataSource<? extends T>... generators) {
            return Fetchers.next(this).toItemConsumer().<T>ofEach(generators);
        }

        @Override
        default Integer next() {
            return nextLength();
        }

        @Override
        @Deprecated
        default ItemConsumer toItemConsumer() {
            return this;
        }

        @Override
        @Deprecated
        default Generator<Integer> toGenerator() {
            return this;
        }

        @Override
        default FlConsumer fluentFetcher() {
            return FlFetcher.wrap(this);
        }
    }
}
