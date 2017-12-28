package org.cthul.fixsure.fluents;

import org.cthul.fixsure.*;
import org.cthul.fixsure.api.AbstractStringify;

/**
 *
 */
public interface FlDistribution extends Distribution {

    @Override
    FlRandom toRandomNumbers(long seedHint);
    
    /**
     * @return this
     * @deprecated redundant operation
     */
    @Override
    @Deprecated
    default FlDistribution fluentDistribution() {
        return this;
    }

    @Override
    FlDataSource<Double> values(long seed);
    
    interface FlRandom extends Distribution.RandomNumbers, FlDistribution {

        @Override
        default FlRandom copy() {
            throw new UnsupportedOperationException();
        }

        /**
         * Returns a random integer.
         * @return integer
         */
        default int nextInt() {
            return (int) (Integer.MAX_VALUE * (nextValue() - 0.5));
        }

        /**
         * Returns a random positive integer.
         * @return integer
         */
        default int nextPositiveInt() {
            return (int) (Integer.MAX_VALUE * nextValue());
        }

        /**
         * Returns a random integer, {@code 0 <= i < n}
         * @param n
         * @return integer
         */
        default int nextInt(int n) {
            return (int) (n * nextValue());
        }

        default long nextLong() {
            return (long) (Long.MAX_VALUE * (nextValue() - 0.5));
        }

        default long nextPositiveLong() {
            return (long) (Long.MAX_VALUE * nextValue());
        }

        default long nextLong(long n) {
            return (long) (n * nextValue());
        }

        /**
         * @param seedHint
         * @return this
         * @deprecated redundant operation
         */
        @Deprecated
        @Override
        default FlRandom toRandomNumbers(long seedHint) {
            return this;
        }

        @Override
        default FlRandom fluentDistribution() {
            return this;
        }

        default FlGenerator<Double> values() {
            return fluentData();
        }

        /**
         * @param seed
         * @return values
         * @deprecated seed not supported, use #fluentData
         */
        @Override
        default FlGenerator<Double> values(long seed) {
            return fluentData();
        }
    }
    
    @FunctionalInterface
    interface Template extends FlDistribution {

        @Override
        FlRandom toRandomNumbers(long seedHint);

        @Override
        default FlTemplate<Double> values(long seed) {
            return FlDistribution.template(this, seed);
        }

        @Override
        default Template fluentDistribution() {
            return this;
        }
    }
    
    static FlTemplate<Double> template(Distribution distribution, long seed) {
        return () -> distribution.toRandomNumbers(seed).fluentData();
    }
    
    static FlDistribution wrap(Distribution distribution) {
        if (distribution instanceof FlDistribution) {
            return (FlDistribution) distribution;
        }
        class FlDistributionTemplate extends AbstractStringify implements FlDistribution.Template {
            @Override
            public FlRandom toRandomNumbers(long seedHint) {
                return distribution.toRandomNumbers(seedHint).fluentDistribution();
            }
            @Override
            public StringBuilder toString(StringBuilder sb) {
                return distribution.toString(sb);
            }
        }
        return new FlDistributionTemplate();
    }
    
    static FlRandom wrap(RandomNumbers generator) {
        if (generator instanceof FlRandom) {
            return (FlRandom) generator;
        }
        class FlRandomWrapper extends AbstractStringify implements FlRandom {
            @Override
            public double nextValue() {
                return generator.nextValue();
            }
            @Override
            public FlRandom copy() {
                return wrap(generator.copy());
            }
            @Override
            public StringBuilder toString(StringBuilder sb) {
                return generator.toString(sb);
            }
        }
        return new FlRandomWrapper();
    }
}
