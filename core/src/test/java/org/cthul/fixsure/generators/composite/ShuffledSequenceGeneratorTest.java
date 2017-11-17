package org.cthul.fixsure.generators.composite;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.cthul.fixsure.distributions.DistributionRandomizer;
import org.cthul.fixsure.fluents.FlSequence;
import org.cthul.fixsure.generators.primitives.ConsecutiveIntegersSequence;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 */
public class ShuffledSequenceGeneratorTest {
    
    public ShuffledSequenceGeneratorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        DistributionRandomizer.setSeed(ShuffledSequenceGeneratorTest.class);
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void test_shuffle_50() {
        FlSequence<Integer> ints = ConsecutiveIntegersSequence.consecutiveIntegers(3, 7, 3+7*50);
        List<Integer> ordered = ints.first(50);
        List<Integer> shuffled = ints.shuffle().first(50);
        assertThat(shuffled, hasSize(50));
//        assertThat(shuffled, containsInAnyOrder(ordered));
    }
    
    @Test
    public void test_large_sequence() {
        Set<Integer> set = new HashSet<>();
        ConsecutiveIntegersSequence.consecutiveIntegers()
                .shuffle().first(1000000)
                .forEach(i -> assertThat(""+i, set.add(i), is(true)));
    }
}
