package org.cthul.fixsure.generators.primitive;

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
public class CharactersGeneratorTest {
    
    @BeforeClass
    public static void setUpClass() {
        DistributionRandom.setSeed(CharactersGeneratorTest.class);
    }
    
    @Before
    public void setUp() {
        DistributionRandom.resetSeed();
    }

    @Test
    public void test_generated_characters() {
        CharactersGenerator cg = CharactersGenerator.characters('a', 'j');
        for (int i = 0; i < 16; i++) {
            assertThat(cg.next(), inRange('a', 'j'));
        }
    }
    
    @Test
    public void test_range_min() {
        CharactersGenerator cg = CharactersGenerator.characters('i', 'l');
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
        CharactersGenerator cg = CharactersGenerator.characters('i', 'l');
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