package org.cthul.fixsure.generators.composite;

import org.cthul.fixsure.GeneratorException;
import org.cthul.fixsure.fluents.FlGenerator;
import org.cthul.fixsure.generators.primitives.RandomIntegersGenerator;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 */
public class DistinctGeneratorTest {
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @Test
    public void test_next() {
        FlGenerator<Integer> gen = DistinctGenerator.distinct(RandomIntegersGenerator.integers(10));
        assertThat(gen.next(10), containsInAnyOrder(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
        try {
            gen.next();
            assertThat("Expected fail", false);
        } catch (GeneratorException e) {
            // expected
        }
    }

}