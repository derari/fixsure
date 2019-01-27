package org.cthul.fixsure.fluents;

import org.cthul.fixsure.Cardinality;
import org.cthul.fixsure.DataSource;
import org.cthul.fixsure.fetchers.Fetchers;
import org.cthul.fixsure.generators.value.ConstantValue;

/**
 *
 */
@FunctionalInterface
public interface FlCardinality extends Cardinality {

    @Override
    FlFetcher toFetcher();
    
    default FlDataSource<FlFetcher> asConsumerSource() {
        return ConstantValue.constant(this::toFetcher);
    }
    
    @FunctionalInterface
    interface FlFetcher extends Fetcher, FlCardinality {
        
        @Override
        default <T> FlValues<T> of(DataSource<T> generator) {
            return Fetchers.next(this).toFetcher().of(generator);
        }

        @Override
        default <T> FlValues<T> ofEach(DataSource<? extends T>... generators) {
            return Fetchers.next(this).toFetcher().<T>ofEach(generators);
        }

        @Override
        default FlSequence<FlFetcher> asConsumerSource() {
            return ConstantValue.<FlFetcher>constant(this);
        }

        @Override
        default FlFetcher fluentCardinality() {
            return this;
        }

        @Override
        @Deprecated
        default FlFetcher toFetcher() {
            return this;
        }
    }
    
    @FunctionalInterface
    interface Template extends FlCardinality {

        @Override
        FlFetcher toFetcher();
        
        @Override
        default FlTemplate<FlFetcher> asConsumerSource() {
            return FlCardinality.super.asConsumerSource().snapshot();
        }

        @Override
        default Template fluentCardinality() {
            return this;
        }
    }
    
    static FlFetcher wrap(Fetcher consumer) {
        return new FlFetcher() {
            @Override
            public int nextLength() {
                return consumer.nextLength();
            }
            @Override
            public <T> FlValues<T> of(DataSource<T> generator) {
                return consumer.of(generator).fluentData();
            }
            @Override
            public <T> FlValues<T> ofEach(DataSource<? extends T>... generators) {
                return consumer.<T>ofEach(generators).fluentData();
            }
        };
    }
}
