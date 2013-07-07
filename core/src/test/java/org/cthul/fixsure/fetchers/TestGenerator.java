package org.cthul.fixsure.fetchers;

import org.cthul.fixsure.base.GeneratorBase;

/**
 *
 */
public class TestGenerator extends GeneratorBase<Integer> {

    public static TestGenerator gen() {
        return new TestGenerator();
    }

    int i = 0;

    @Override
    public Integer next() {
        return i++;
    }
    
}
