package org.cthul.fixsure;

import org.cthul.fixsure.fluents.FlDistribution;

/**
 * Provides random values between 0 and 1 in some distribution.
 */
@FunctionalInterface
public interface Distribution {

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
     * Provides access to fluent methods on this distribution.
     * @return fluent
     */
    default FlDistribution fluentDistribution() {
        return FlDistribution.wrap(this);
    }
    
    @FunctionalInterface
    interface RandomNumbers extends Distribution, Generator<Double>, Typed<Double> {
    
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
         * @param seedHint
         * @return itself
         * @deprecated redundant operation
         */
        @Deprecated
        @Override
        default RandomNumbers toRandomNumbers(long seedHint) {
            return this;
        }

        /**
         * @param seed
         * @return this
         * @deprecated redundant operation
         */
        @Deprecated
        @Override
        default Generator<Double> values(long seed) {
            return this;
        }
        
        @Override
        default FlDistribution.FlRandom fluentDistribution() {
            return FlDistribution.wrap(this);
        }

        @Override
        default Double next() {
            return nextValue();
        }

        @Override
        default Class<Double> getValueType() {
            return Double.class;
        }
    }
}
