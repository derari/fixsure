package org.cthul.fixsure.generators.primitives;

import org.cthul.fixsure.fluents.FlSequence;
import org.cthul.fixsure.generators.value.ItemsSequence;
import static org.cthul.fixsure.generators.value.ItemsSequence.*;
import org.cthul.fixsure.fluents.FlTemplate;

/**
 *
 */
public class TestValues {
    
    private static final ItemsSequence<Integer> DEFAULT_INTS = sequence(
            0, 1, -1, 2, -2, 255, -256, 1 << 16, -(1 << 16), 
            Integer.MAX_VALUE, Integer.MIN_VALUE);
    
    private static final ItemsSequence<Double> DEFAULT_DOUBLES = sequence(
            0.0, 9/10.0, -9/10.0, 1.0, -1.0, 1.5, -1.5, 
            2.0, -2.0, Math.PI, -Math.PI, 255.0, -256.0,
            Double.valueOf(1 << 16), Double.valueOf(-(1 << 16)));
    
    private static final ItemsSequence<Double> SPECIAL_DOUBLES = sequence(
            Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    
    private static final FlTemplate<Integer> TEST_INTS = defaultIntegers().then(IntegersGenerator.integers());
    
    private static final FlTemplate<Double> TEST_DOUBLES = defaultDoubles().then(DoublesGenerator.doubles());
    
    private static final FlTemplate<Double> TEST_SPECIAL_DOUBLES = defaultDoubles().then(specialDoubles(), DoublesGenerator.doubles());

    public static ItemsSequence<Integer> defaultIntegers() {
        return DEFAULT_INTS;
    }
    
    public static FlTemplate<Integer> testIntegers() {
        return TEST_INTS;
    }
    
    public static FlSequence<Double> defaultDoubles() {
        return DEFAULT_DOUBLES;
    }
    
    public static FlTemplate<Double> testDoubles() {
        return TEST_DOUBLES;
    }
    
    public static FlSequence<Double> specialDoubles() {
        return SPECIAL_DOUBLES;
    }
    
    public static FlTemplate<Double> specialTestDoubles() {
        return TEST_SPECIAL_DOUBLES;
    }
    
}
