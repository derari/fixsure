package org.cthul.fixsure.generators.composite;

import java.util.List;
import org.cthul.fixsure.DataSource;
import org.cthul.fixsure.generators.value.ItemsSequence;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import org.junit.Test;

/**
 *
 */
public class MergingGeneratorTest {

    @Test
    public void test_merge() {
        Integer a5 = new Integer(5);
        Integer b5 = new Integer(5);
        DataSource<Integer> a = ItemsSequence.sequence(1,  4, a5);
        DataSource<Integer> b = ItemsSequence.sequence(3, b5,  8);
        DataSource<Integer> c = ItemsSequence.sequence(2,  6,  7);
        
        List<Integer> merged = MergingGenerator.merge(a, b, c).all();
        
        assertThat(merged, contains(1, 2, 3, 4, 5, 5, 6, 7, 8));
        assertThat(merged.get(4), is(sameInstance(a5)));
        assertThat(merged.get(5), is(sameInstance(b5)));
    }
}