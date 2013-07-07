package org.cthul.fixsure.generators.primitive;

import org.cthul.fixsure.generators.value.ItemsGenerator;
import org.cthul.fixsure.generators.composite.GeneratorQueue;
import org.hamcrest.Factory;
import static org.cthul.fixsure.generators.value.ItemsGenerator.*;

/**
 *
 * @author Arian Treffer
 */
public class TestValuesGenerator {
    
    private static final ItemsGenerator<Integer> DEFAULT_INTS = from(
            0, 1, -1, 2, -2, 255, -256, 1 << 16, -(1 << 16)-1, 
            Integer.MAX_VALUE, Integer.MIN_VALUE);
    
    private static final ItemsGenerator<Double> DEFAULT_DOUBLES = from(
            0.0, 9/10.0, -9/10.0, 1.0, -1.0, 1.5, -1.5, 
            2.0, -2.0, Math.PI, -Math.PI, 255.0, -256.0,
            Double.valueOf(1 << 16), Double.valueOf(-(1 << 16)-1));
    
    private static final ItemsGenerator<Double> SPECIAL_DOUBLES = from(
            Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

    @Factory
    public static ItemsGenerator<Integer> defaultIntegers() {
        return DEFAULT_INTS.newGenerator();
    }
    
    @Factory
    public static GeneratorQueue<Integer> testIntegers() {
        return GeneratorQueue.queue(defaultIntegers(), IntegersGenerator.integers());
    }
    
    @Factory
    public static ItemsGenerator<Double> defaultDoubles() {
        return DEFAULT_DOUBLES.newGenerator();
    }
    
    @Factory
    public static GeneratorQueue<Double> testDoubles() {
        return GeneratorQueue.queue(defaultDoubles()); //.then(DoublesGenerator.integers());
    }
    
    @Factory
    public static ItemsGenerator<Double> specialDoubles() {
        return SPECIAL_DOUBLES.newGenerator();
    }
    
    @Factory
    public static GeneratorQueue<Double> specialTestDoubles() {
        return GeneratorQueue.queue(defaultDoubles(), specialDoubles()); //.then(DoublesGenerator.integers());
    }
    
}
