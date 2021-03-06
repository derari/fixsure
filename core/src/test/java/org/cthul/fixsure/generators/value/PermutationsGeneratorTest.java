package org.cthul.fixsure.generators.value;

import java.util.List;
import org.cthul.fixsure.fluents.FlGenerator;
import org.hamcrest.Matcher;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
        List<Integer[]> list = permutations.all();
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
    
    @Test
    public void test_toString() {
        FlGenerator<Integer[]> g = new PermutationsGenerator<>(i(1, 2, 3, 4));
        assertThat(g.toString(), is("{1,2,3,4}.permutations()"));
    }
}