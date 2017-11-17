package org.cthul.fixsure.fetchers;

import org.cthul.fixsure.fluents.FlGenerator;

/**
 *
 */
public class TestGenerator implements FlGenerator<Integer> {

    public static TestGenerator gen() {
        return new TestGenerator();
    }

    int i = 0;

    @Override
    public Integer next() {
        return i++;
    }
}
