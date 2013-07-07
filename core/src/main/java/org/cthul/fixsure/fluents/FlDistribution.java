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
     * Returns a random integer, {@code 0 <= i < n}
     * @param n
     * @return integer
     */
    int nextInt(int n);
    
    long nextLong();
    
    long nextLong(long n);
    
}
