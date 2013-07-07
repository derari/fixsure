package org.cthul.fixsure.generators.composite;

import java.util.List;
import org.cthul.fixsure.fluents.FlGenerator;
import org.cthul.fixsure.generators.value.PermutationsGenerator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

/**
 *
 * @author at
 */
public class RepeatingGeneratorTest {
    
    public RepeatingGeneratorTest() {
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

    /**
     * Test of repeat method, of class RepeatingGenerator.
     */
    @Test
    public void test_repeat() {
        FlGenerator<Integer[]> gen = RepeatingGenerator.repeat(PermutationsGenerator.permutations(1, 2));
        assertThat(gen.getValueType(), is((Object) Integer[].class));
        
        List<Integer[]> list = gen.next(5);
        assertThat(list.get(0), is(i(1, 2)));
        assertThat(list.get(1), is(i(2, 1)));
        assertThat(list.get(2), is(i(1, 2)));
        assertThat(list.get(3), is(i(2, 1)));
        assertThat(list.get(4), is(i(1, 2)));
        
    }
    
    private static Integer[] i(Integer... i) {
        return i;
    }

}