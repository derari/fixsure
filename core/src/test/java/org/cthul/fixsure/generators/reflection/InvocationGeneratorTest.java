package org.cthul.fixsure.generators.reflection;

import org.cthul.fixsure.Values;
import org.cthul.fixsure.generators.primitive.IntegersGenerator;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

/**
 *
 */
public class InvocationGeneratorTest {
    
    @Test
    public void test_invocations() {
        Values<Integer> values = IntegersGenerator.integers().next(5);
        Values<String> strings = InvocationGenerator.<String>invoke(values, "toString").all();
        for (int i = 0; i < values.size(); i++) {
            assertThat("#"+i, 
                    String.valueOf(values.get(i)), 
                    is(strings.get(i)));
        }
    }
}