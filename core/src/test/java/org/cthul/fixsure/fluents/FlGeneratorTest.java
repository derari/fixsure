package org.cthul.fixsure.fluents;

import org.cthul.fixsure.Fixsure;
import org.cthul.fixsure.Generator;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.junit.Test;

/**
 *
 */
public class FlGeneratorTest {
    
    @Test
    public void test_transform() {
        FlTemplate<Integer> ints = Fixsure.sequence(1, 2, 3, 4);
        String s1 = ints.transform(this::initialString);
        assertThat(s1, is("123"));
        String s2 = ints.newGenerator().transform(this::initialString);
        assertThat(s2, is("123"));
    }
    
    public String initialString(FlDataSource<? extends Object> data) {
        Generator<?> gen = data.toGenerator();
        return "" + gen.next() + gen.next() + gen.next();
    }
    
}
