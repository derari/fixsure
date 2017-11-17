package org.cthul.fixsure.generators.primitives;

import org.cthul.fixsure.distributions.DistributionRandomizer;
import org.cthul.fixsure.fluents.FlGenerator;
import org.cthul.fixsure.generators.primitives.CharactersGenerator;
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
public class CharactersGeneratorTest {
    
    @BeforeClass
    public static void setUpClass() {
        DistributionRandomizer.setSeed(CharactersGeneratorTest.class);
    }
    
    @Before
    public void setUp() {
        DistributionRandomizer.setSeed(CharactersGeneratorTest.class);
    }

    @Test
    public void test_generated_characters() {
        FlGenerator<Character> cg = CharactersGenerator.characters('a', 'j').newGenerator();
        for (int i = 0; i < 16; i++) {
            assertThat(cg.next(), inRange('a', 'j'));
        }
    }
    
    @Test
    public void test_range_min() {
        FlGenerator<Character> cg = CharactersGenerator.characters('i', 'l').newGenerator();
        boolean success = false;
        for (int i = 0; i < 16; i++) {
            if (cg.next() == 'i') {
                success = true;
                break;
            }
        }
        assertThat("'i' was found", success);
    }
    
    @Test
    public void test_range_max() {
        FlGenerator<Character> cg = CharactersGenerator.characters('i', 'l').newGenerator();
        boolean success = false;
        for (int i = 0; i < 16; i++) {
            if (cg.next() == 'l') {
                success = true;
                break;
            }
        }
        assertThat("'l' was found", success);
    }
    
    protected CombinableMatcher<Character> inRange(char min, char max) {
        return both(greaterThanOrEqualTo(min)).and(lessThanOrEqualTo(max));
    }
}