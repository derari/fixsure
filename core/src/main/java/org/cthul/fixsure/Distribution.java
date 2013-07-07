package org.cthul.fixsure;

/**
 * Provides random values between 0 and 1 in some distribution.
 */
public interface Distribution extends Generator<Double> {
    
    /**
     * Produces a random value {@code x}, with {@code 0 <= x < 1}.
     * @return random value
     */
    double nextValue();
    
    /**
     * Implements the cumulative distribution function.
     * <p>
     * Maps a value between 0 and 1 to a value between 0 and 1.
     * 
     * @param x input value
     * @return mapped value
     */
    double map(double x);
    
}
