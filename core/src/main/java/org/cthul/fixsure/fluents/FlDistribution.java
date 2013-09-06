package org.cthul.fixsure.fluents;

import org.cthul.fixsure.Distribution;

/**
 *
 */
public interface FlDistribution extends Distribution, FlGenerator<Double> {
    
    /**
     * Returns a random integer.
     * @return integer
     */
    int nextInt();
    
    /**
     * Returns a random positive integer.
     * @return integer
     */
    int nextPositiveInt();
    
    /**
     * Returns a random integer, {@code 0 <= i < n}
     * @param n
     * @return integer
     */
    int nextInt(int n);
    
    long nextLong();
    
    long nextPositiveLong();
    
    long nextLong(long n);
    
}
