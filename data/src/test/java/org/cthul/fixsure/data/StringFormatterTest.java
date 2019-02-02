package org.cthul.fixsure.data;

import static org.cthul.fixsure.Fixsure.integers;
import org.cthul.fixsure.factory.Factories;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 */
public class StringFormatterTest {

    Factories factories = Factories.build()
            .newFactory("Greeting")
                .assign("number").to(integers(1,6))
                .assign("greeting").to(StringFormatter.formatString("%1$s Number %:number$Ir", "Hello"))
                .build(vm -> vm.str("greeting"))
            .toFactories();
    
    @Test
    public void test() {
        String string = factories.create("Greeting");
        assertThat(string, is("Hello Number IV"));
    }
    
}
