package org.cthul.fixsure.fetchers;

import java.util.List;
import org.cthul.fixsure.distributions.DistributionRandomizer;
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
        DistributionRandomizer.setSeed(SimpleFetcherTest.class);
    }
    
    @Before
    public void setUp() {
        DistributionRandomizer.setSeed(SimpleFetcherTest.class);
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
        List<Integer> list = Fetchers.few().toFetcher().of(TestGenerator.gen());
        assertThat(list, hasSize(between(3, 6)));
        assertThat(list.subList(0, 3), contains(0, 1, 2));
    }
    
    @Test
    public void test_some() {
        List<Integer> list = Fetchers.some().toFetcher().of(TestGenerator.gen());
        assertThat(list, hasSize(between(5, 9)));
        assertThat(list.subList(0, 5), contains(0, 1, 2, 3, 4));
    }
    
    @Test
    public void test_several() {
        List<Integer> list = Fetchers.several().toFetcher().of(TestGenerator.gen());
        assertThat(list, hasSize(between(8, 17)));
        assertThat(list.subList(0, 8), contains(0, 1, 2, 3, 4, 5, 6, 7));
    }
    
    @Test
    public void test_many() {
        List<Integer> list = Fetchers.many().toFetcher().of(TestGenerator.gen());
        assertThat(list, hasSize(between(96, 129)));
    }
    
    protected CombinableMatcher<Integer> between(int low, int high) {
        return both(greaterThanOrEqualTo(low)).and(lessThan(high));
    }
}