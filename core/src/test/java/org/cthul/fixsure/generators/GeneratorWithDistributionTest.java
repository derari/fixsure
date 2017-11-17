package org.cthul.fixsure.generators;

import java.util.List;
import org.cthul.fixsure.distributions.DistributionRandomizer;
import static org.cthul.fixsure.generators.primitives.IntegersGenerator.integers;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author C5173086
 */
public class GeneratorWithDistributionTest {
    
    public GeneratorWithDistributionTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        DistributionRandomizer.setSeed(GeneratorWithDistributionTest.class);
    }
    
    @After
    public void tearDown() {
    }

    private static List<Integer> list1 = null;
    private static List<Integer> list2 = null;
    
    @Test
    public void test_integers_1() {
        list1 = integers().many();
        if (list2 != null) {
            assertThat(list1, is(list2));
        }
    }
    
    @Test
    public void test_integers_2() {
        list2 = integers().many();
        if (list1 != null) {
            assertThat(list1, is(list2));
        }
    }
    
    @Test
    public void test_integers() {
        List<Integer> l1 = integers().many(), l2 = integers().many();
        assertThat(l1, is(l2));
    }
    
//    @Test
//    public void test_copy() {
//        FlGenerator<Integer> ints = integers().newGenerator();
//        
//        FlTemplate<Integer> tmpl = ints.snapshot();
//        assertThat(tmpl.first(100), is(tmpl.first(100)));
//        
//        ints.next(3);
//        FlTemplate<Integer> tmpl2 = ints.snapshot();
//        assertThat(tmpl2.first(100), is(tmpl2.first(100)));
//        
//        FlGenerator<Integer> ints1 = tmpl.newGenerator();
//        ints1.next(3);
//        assertThat(tmpl2.first(100), is(ints1.next(100)));
//    }
}
