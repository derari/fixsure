package org.cthul.fixsure;

import org.junit.Test;

/**
 *
 */
public class ShuffleTest {

    @Test
    public void test() {
        final int[] ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        final int p = 7;
        int n = 0;
        for (int i = 0; i < ints.length; i++) {
            System.out.println(ints[n]);
            n = (n+p) % ints.length;
        }
    }
    
}
