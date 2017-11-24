package org.cthul.fixsure.generators.composite;

import static org.cthul.fixsure.Fixsure.sequence;
import org.cthul.fixsure.fluents.FlGenerator;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import org.junit.Test;

/**
 *
 */
public class GeneratorQueueTest {

    @Test
    public void testQueue() {
        FlGenerator<Integer> g = sequence(1, 2).newGenerator().then(sequence(5, 6)).then(sequence(10, 11));
        assertThat(g.all(), hasItems(1, 2, 5, 6, 10, 11));
    }
    
    @Test
    public void testToString() {
        FlGenerator<Integer> g = sequence(1, 2).newGenerator().then(sequence(5, 6)).then(sequence(10, 11));
        assertThat(g.toString(), is("{{1,2}[0],{5,6}[0],{10,11}[0]}"));
    }
    
}
