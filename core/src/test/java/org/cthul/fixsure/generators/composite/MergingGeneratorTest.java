package org.cthul.fixsure.generators.composite;

import java.util.List;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.generators.value.ItemsGenerator;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

/**
 *
 */
public class MergingGeneratorTest {

    @Test
    public void test_merge() {
        Integer a5 = new Integer(5);
        Integer b5 = new Integer(5);
        Generator<Integer> a = ItemsGenerator.from(1,  4, a5);
        Generator<Integer> b = ItemsGenerator.from(3, b5,  8);
        Generator<Integer> c = ItemsGenerator.from(2,  6,  7);
        
        List<Integer> merged = MergingGenerator.merge(a, b, c).all();
        
        assertThat(merged, contains(1, 2, 3, 4, 5, 5, 6, 7, 8));
        assertThat(merged.get(4), is(sameInstance(a5)));
        assertThat(merged.get(5), is(sameInstance(b5)));
    }
}