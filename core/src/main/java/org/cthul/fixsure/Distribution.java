package org.cthul.fixsure;

import org.cthul.fixsure.api.Stringify;
import org.cthul.fixsure.fluents.FlDistribution;

/**
 * Describes a distribution of random values between 0 and 1.
 */
@FunctionalInterface
public interface Distribution extends Typed<Double>, Stringify {

    /**
     * If this is a random number generator, returns itself;
     * otherwise returns a new random number generator.
     * @param seedHint generators created with the same seed should produce the same values
     * @return random number generator
     */
    RandomNumbers toRandomNumbers(long seedHint);
    
    /**
     * Converts this distribution into a data source.
     * @param seed generators created with the same seed should produce the same values
     * @return data source
     */
    default DataSource<Double> values(long seed) {
        return FlDistribution.template(this, seed);
    }

    /**
     * Provides access to the {@linkplain FlDistribution fluent distribution} interface.
     * @return fluent
     */
    default FlDistribution fluentDistribution() {
        return FlDistribution.wrap(this);
    }

    /**
     * Type is always double.
     * @return {@code Double.class}
     */
    @Override
    default Class<Double> getValueType() {
        return Double.class;
    }
    
    /**
     * A random number generator based on a {@link Distribution}.
     * <p>
     * The values generated are random doubles between 0 and 1.
     */
    @FunctionalInterface
    interface RandomNumbers extends Distribution, Generator<Double> {
    
        /**
         * Produces a random value {@code x}, with {@code 0 <= x < 1}.
         * @return random value
         */
        double nextValue();

        /**
         * Optional operation.
         * @return copy of current state
         * @throws UnsupportedOperationException
         */
        default RandomNumbers copy() {
            throw new UnsupportedOperationException();
        }

        /**
         * Returns self.
         * @param seedHint
         * @return this
         * @deprecated Redundant operation
         */
        @Deprecated
        @Override
        default RandomNumbers toRandomNumbers(long seedHint) {
            return this;
        }

        /**
         * Returns self.
         * @param seed
         * @return this
         * @deprecated Redundant operation
         */
        @Deprecated
        @Override
        default Generator<Double> values(long seed) {
            return this;
        }
        
        /**
         * Provides access to the {@linkplain FlDistribution.FlRandom fluent random} interface.
         * @return fluent
         */
        @Override
        default FlDistribution.FlRandom fluentDistribution() {
            return FlDistribution.wrap(this);
        }

        /**
         * Alias for {@link #nextValue()}.
         * @return random value
         */
        @Override
        default Double next() {
            return nextValue();
        }
    }
}
