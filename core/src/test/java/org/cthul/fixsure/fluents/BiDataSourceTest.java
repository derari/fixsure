package org.cthul.fixsure.fluents;

import static org.cthul.fixsure.Fixsure.sequence;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import org.junit.Test;

/**
 *
 */
public class BiDataSourceTest {
    
    String[] words = {"a", "short", "hello world"};
    Integer[] lengths = {1, 5, 11};
        
    @Test
    public void test_split_function() {
        BiSequence<String, Integer> wordsWithLength = sequence(words).split(String::length);    
        assertThat(wordsWithLength.onlySecond().value(2), is(11));
    }
    
    @Test
    public void test_toString() {
        BiSequence<String, Integer> wordsWithLength = sequence(words).split(String::length);    
        assertThat(wordsWithLength.toString(), startsWith("{a,short,hello world}.split(BiDataSourceTest$$Lambda"));
    }
    
    @Test
    public void test_generator_with() {
        BiGenerator<String, Integer> wwL = sequence(words).newGenerator().with(sequence(lengths));
        FlGenerator<String> w2 = wwL.map((w, l) -> w + l);
        assertThat(w2.all(), hasItems("a1", "short5", "hello world11"));
        assertThat(w2.toString(), startsWith("({a,short,hello world}[3];{1,5,11}[3]).map(BiDataSourceTest$$Lambda"));
    }
}
