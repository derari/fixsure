package org.cthul.fixsure.generators.value;

import java.util.function.Supplier;
import org.cthul.fixsure.api.Factory;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.Sequence;
import org.cthul.fixsure.api.Stringify;
import org.cthul.fixsure.api.AbstractStringify;
import org.cthul.fixsure.distributions.DistributionRandomizer;
import org.cthul.fixsure.fluents.FlGenerator;
import org.cthul.fixsure.fluents.FlSequence;
import org.cthul.fixsure.fluents.FlTemplate;

/**
 *
 */
public class ConstantValue<T> extends AbstractStringify implements FlSequence<T> {
    
    private static final ConstantValue NULLS = new ConstantValue(null);
    
    /**
     * Creates an unbounded sequence of only one value.
     * @param <T>
     * @param value
     * @return constant value sequence
     */
    @Factory
    public static <T> ConstantValue<T> constant(T value) {
        return new ConstantValue<>(value);
    }
    
    @Factory
    public static <T> FlTemplate<T> constant(Supplier<? extends T> valueSupplier) {
        return () -> constant(valueSupplier.get()).newGenerator();
    }
    
    /**
     * Returns an unbounded sequence of {@code null} values 
     * @param <T>
     * @return sequence of {@code null} values 
     */
    @Factory
    public static <T> ConstantValue<T> nullValues() {
        return NULLS;
    }
    
    private final T value;

    public ConstantValue(T value) {
        this.value = value;
    }

    @Override
    public Class<T> getValueType() {
        if (value == null) {
            return null;
        }
        return (Class) value.getClass();
    }

    @Override
    public T value(long n) {
        return value;
    }

    @Override
    public long length() {
        return Sequence.L_NEGATIVE_INDICES;
    }

    @Override
    public FlGenerator<T> newGenerator() {
        return Generator.generate(getValueType(), () -> value);
    }

    @Override
    public long randomSeedHint() {
        return DistributionRandomizer.toSeed(getClass());
    }

    @Override
    public StringBuilder toString(StringBuilder sb) {
        return Stringify.toString(value, sb.append("Repeat(")).append(')');
    }
}
