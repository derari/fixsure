package org.cthul.fixsure.generators.primitive;

import org.cthul.fixsure.generators.primitive.StringsGenerator;
import org.cthul.fixsure.distributions.DistributionRandom;
import org.hamcrest.core.CombinableMatcher;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

/**
 *
 */
public class StringsGeneratorTest {
    
    @BeforeClass
    public static void setUpClass() {
        DistributionRandom.setSeed(StringsGeneratorTest.class);
    }
    
    @Before
    public void setUp() {
        DistributionRandom.resetSeed();
    }
    
    @Test
    public void test_generated_strings() {
        StringsGenerator sg = StringsGenerator.strings();
        for (int i = 0; i < 3; i++) {
            String s = sg.next();
            assertThat(s, is(notNullValue()));
            assertThat(s.length(), is(StringsGenerator.DEFAULT_LENGTH));
            int j = 0;
            for (char c: s.toCharArray()) {
                assertThat(i + "/" + j, 
                        c, inRange(' ', '~'));
                j++;
            }
        }
    }

    @Test
    public void test_generated_letter_strings() {
        StringsGenerator sg = StringsGenerator.strings('A', 'Z', 5);
        for (int i = 0; i < 3; i++) {
            String s = sg.next();
            assertThat(s, is(notNullValue()));
            assertThat(s.length(), is(5));
            int j = 0;
            for (char c: s.toCharArray()) {
                assertThat(i + "/" + j, 
                        c, inRange('A', 'Z'));
                j++;
            }
        }
    }

    protected CombinableMatcher<Character> inRange(char min, char max) {
        return both(greaterThanOrEqualTo(min)).and(lessThanOrEqualTo(max));
    }
}