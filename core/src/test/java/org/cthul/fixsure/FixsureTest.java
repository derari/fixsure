package org.cthul.fixsure;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import org.junit.Test;

/**
 *
 */
public class FixsureTest {

    @Test
    public void test_fixsure() {
        Fixsure.integers(10).several().forEach(i -> {
            assertThat(i, lessThan(10));
        });
    }
}
