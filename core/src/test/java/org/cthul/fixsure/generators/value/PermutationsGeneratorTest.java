package org.cthul.fixsure.generators.value;

import org.cthul.fixsure.generators.value.PermutationsGenerator;
import java.util.List;
import org.cthul.fixsure.fetchers.EagerFetcher;
import org.cthul.fixsure.fluents.FlGenerator;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

public class PermutationsGeneratorTest {
    
    public PermutationsGeneratorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    private static Integer[] i(Integer... i) {
        return i;
    }
    
    @Test
    public void test_permutations2() {
        FlGenerator<Integer[]> permutations = new PermutationsGenerator<>(i(1, 2));
        List<Integer[]> list = EagerFetcher.all(permutations);
        assertThat(list, (Matcher) contains(i(1, 2), i(2, 1)));
    }
    
    @Test
    public void test_permutations3() {
        FlGenerator<Integer[]> g = new PermutationsGenerator<>(i(1, 2, 3));
        List<Integer[]> list = g.next(6);
        assertThat(list, (Matcher) contains(
                i(1, 2, 3), i(1, 3, 2), i(3, 1, 2), 
                i(3, 2, 1), i(2, 3, 1), i(2, 1, 3)
                ));
    }
    
    @Test
    public void test_permutations4() {
        FlGenerator<Integer[]> g = new PermutationsGenerator<>(i(1, 2, 3, 4));
        List<Integer[]> list = g.next(24);
        assertThat(list, (Matcher) contains(
                i(1, 2, 3, 4), i(1, 2, 4, 3), i(1, 4, 2, 3), i(4, 1, 2, 3),
                i(4, 1, 3, 2), i(1, 4, 3, 2), i(1, 3, 4, 2), i(1, 3, 2, 4), 
                i(3, 1, 2, 4), i(3, 1, 4, 2), i(3, 4, 1, 2), i(4, 3, 1, 2), 
                i(4, 3, 2, 1), i(3, 4, 2, 1), i(3, 2, 4, 1), i(3, 2, 1, 4), 
                i(2, 3, 1, 4), i(2, 3, 4, 1), i(2, 4, 3, 1), i(4, 2, 3, 1), 
                i(4, 2, 1, 3), i(2, 4, 1, 3), i(2, 1, 4, 3), i(2, 1, 3, 4)
                ));
    }
}