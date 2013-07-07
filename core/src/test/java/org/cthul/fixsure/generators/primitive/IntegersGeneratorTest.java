package org.cthul.fixsure.generators.primitive;

import org.cthul.fixsure.generators.primitive.IntegersGenerator;
import org.cthul.fixsure.distributions.DistributionRandom;
import org.hamcrest.core.CombinableMatcher;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

/**
 *
 * @author Arian Treffer
 */
public class IntegersGeneratorTest {
    
    public IntegersGeneratorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        DistributionRandom.setSeed(IntegersGeneratorTest.class);
    }
    
    @Before
    public void setUp() {
        DistributionRandom.resetSeed();
    }

    @Test
    public void test_generated_integers() {
        IntegersGenerator ig = IntegersGenerator.integers();
        for (int i = 0; i < 16; i++) {
            assertThat(ig.next(), between(IntegersGenerator.DEFAULT_LOW, IntegersGenerator.DEFAULT_HIGH));
        }
    }
    
    @Test
    public void test_generated_ranged_integers() {
        IntegersGenerator ig = IntegersGenerator.integers(17, 20);
        for (int i = 0; i < 16; i++) {
            assertThat(ig.next(), between(17, 20));
        }
    }
    
    protected CombinableMatcher<Integer> between(int low, int high) {
        return both(greaterThanOrEqualTo(low)).and(lessThan(high));
    }
}