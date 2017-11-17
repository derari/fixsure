package org.cthul.fixsure.fluents;

import org.cthul.fixsure.DataSource;
import org.cthul.fixsure.Fetcher;
import org.cthul.fixsure.fetchers.Fetchers;
import org.cthul.fixsure.generators.value.ConstantValue;

/**
 *
 */
@FunctionalInterface
public interface FlFetcher extends Fetcher {

    @Override
    FlConsumer toItemConsumer();
    
    default FlDataSource<FlConsumer> asConsumerSource() {
        return ConstantValue.constant(this::toItemConsumer);
    }
    
    @FunctionalInterface
    interface FlConsumer extends Fetcher.ItemConsumer, FlFetcher {
        
        @Override
        default <T> FlValues<T> of(DataSource<T> generator) {
            return Fetchers.next(this).toItemConsumer().of(generator);
        }

        @Override
        default <T> FlValues<T> ofEach(DataSource<? extends T>... generators) {
            return Fetchers.next(this).toItemConsumer().<T>ofEach(generators);
        }

        @Override
        default FlSequence<FlConsumer> asConsumerSource() {
            return ConstantValue.<FlConsumer>constant(this);
        }

        @Override
        default FlConsumer fluentFetcher() {
            return this;
        }

        @Override
        default FlConsumer toItemConsumer() {
            return this;
        }
    }
    
    @FunctionalInterface
    interface Template extends FlFetcher {

        @Override
        FlConsumer toItemConsumer();
        
        @Override
        default FlTemplate<FlConsumer> asConsumerSource() {
            return FlFetcher.super.asConsumerSource().snapshot();
        }

        @Override
        default Template fluentFetcher() {
            return this;
        }
    }
    
    static FlConsumer wrap(ItemConsumer consumer) {
        return new FlConsumer() {
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
