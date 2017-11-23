package org.cthul.fixsure.values;

import org.cthul.fixsure.Values;
import org.cthul.fixsure.fluents.FlGenerator;
import org.cthul.fixsure.fluents.FlTemplate;
import static org.cthul.fixsure.generators.primitives.RandomIntegersGenerator.integers;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import org.junit.After;
import org.junit.Test;

/**
 *
 */
public class LazyValuesTest {
    
    public LazyValuesTest() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void test_fetching() {
        Values<Number> numbers = LazyValues.any(3, integers());
    }
    
    @Test
    public void test_integers_are_random() {
        FlGenerator<Integer> gen = integers().newGenerator();
        assertThat(gen.next(100), is(not(gen.next(100))));
    }
    
    @Test
    public void test_cached() {
        FlTemplate<Integer> tmp = integers().cached();
        assertThat(tmp.first(100), is(tmp.first(100)));
        
        FlGenerator<Integer> gen1 = tmp.newGenerator();
        FlGenerator<Integer> gen2 = tmp.newGenerator();
        assertThat(gen1.next(100), is(gen2.next(100)));
    }
}