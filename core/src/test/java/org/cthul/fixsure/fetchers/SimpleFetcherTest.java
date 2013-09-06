package org.cthul.fixsure.fetchers;

import org.cthul.fixsure.fetchers.LazyFetcher;
import java.util.List;
import org.cthul.fixsure.base.GeneratorBase;
import org.cthul.fixsure.distributions.DistributionRandom;
import org.cthul.fixsure.fluents.FlGenerator;
import org.hamcrest.core.CombinableMatcher;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

/**
 *
 */
public class SimpleFetcherTest {

    @BeforeClass
    public static void setUpClass() {
        DistributionRandom.setSeed(SimpleFetcherTest.class);
    }
    
    @Before
    public void setUp() {
        DistributionRandom.resetSeed();
    }

    @Test
    public void test_one() {
        List<Integer> list = Fetchers.one().of(TestGenerator.gen());
        assertThat(list, contains(0));
    }
    
    @Test
    public void test_two() {
        List<Integer> list = Fetchers.two().of(TestGenerator.gen());
        assertThat(list, contains(0, 1));
    }
    
    @Test
    public void test_three() {
        List<Integer> list = Fetchers.three().of(TestGenerator.gen());
        assertThat(list, contains(0, 1, 2));
    }
    
    @Test
    public void test_few() {
        List<Integer> list = Fetchers.few().of(TestGenerator.gen());
        assertThat(list, hasSize(between(3, 5)));
        assertThat(list.subList(0, 3), contains(0, 1, 2));
    }
    
    @Test
    public void test_some() {
        List<Integer> list = Fetchers.some().of(TestGenerator.gen());
        assertThat(list, hasSize(between(5, 8)));
        assertThat(list.subList(0, 5), contains(0, 1, 2, 3, 4));
    }
    
    @Test
    public void test_several() {
        List<Integer> list = Fetchers.several().of(TestGenerator.gen());
        assertThat(list, hasSize(between(8, 17)));
        assertThat(list.subList(0, 8), contains(0, 1, 2, 3, 4, 5, 6, 7));
    }
    
    @Test
    public void test_many() {
        List<Integer> list = Fetchers.many().of(TestGenerator.gen());
        assertThat(list, hasSize(between(96, 129)));
    }
    
    protected CombinableMatcher<Integer> between(int low, int high) {
        return both(greaterThanOrEqualTo(low)).and(lessThan(high));
    }
}