package org.cthul.fixsure.generators.primitives;

import org.cthul.fixsure.distributions.DistributionRandomizer;
import org.cthul.fixsure.fluents.FlGenerator;
import org.cthul.fixsure.fluents.FlTemplate;
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
        DistributionRandomizer.setSeed(IntegersGeneratorTest.class);
    }
    
    @Before
    public void setUp() {
        DistributionRandomizer.setSeed(IntegersGeneratorTest.class);
    }

    @Test
    public void test_generated_integers() {
        FlGenerator<Integer> ig = RandomIntegersGenerator.integers().newGenerator();
        for (int i = 0; i < 16; i++) {
            assertThat(ig.next(), between(RandomIntegersGenerator.DEFAULT_LOW, RandomIntegersGenerator.DEFAULT_HIGH));
        }
    }
    
    @Test
    public void test_generated_ranged_integers() {
        FlTemplate<Integer> ig = RandomIntegersGenerator.integers(17, 20);
        ig.first(20).forEach(i -> {
            assertThat(i, between(17, 20));
        });
    }
    
    protected CombinableMatcher<Integer> between(int low, int high) {
        return both(greaterThanOrEqualTo(low)).and(lessThan(high));
    }
}