package org.cthul.fixsure.generators;

import org.cthul.fixsure.Sequence;
import org.cthul.fixsure.SequenceLength;
import static org.cthul.fixsure.SequenceLength.isInRange;
import org.cthul.fixsure.api.AbstractStringify;
import org.cthul.fixsure.distributions.DistributionRandomizer;
import org.cthul.fixsure.fluents.FlSequence;

/**
 *
 */
public abstract class AnonymousSequence<T> extends AbstractStringify implements FlSequence<T> {
    
    private final long length;
    private final Sequence<?> source;

    public AnonymousSequence(long length) {
        this(null, length);
    }

    public AnonymousSequence(SequenceLength length) {
        this(null, length);
    }

    public AnonymousSequence(Sequence source) {
        this(source, source);
    }

    public AnonymousSequence(Sequence<?> source, long length) {
        this.source = source;
        this.length = length;
    }

    public AnonymousSequence(Sequence<?> source, SequenceLength length) {
        this.source = source;
        this.length = SequenceLength.toLong(length);
    }

    @Override
    public long length() {
        return length;
    }
    
    protected void assertInRange(long n) {
        if (!isInRange(n, this)) {
            throw new IndexOutOfBoundsException("" + n);
        }
    }

    @Override
    public Class<T> getValueType() {
        return GeneratorTools.typeOf(source);
    }

    @Override
    public long randomSeedHint() {
        if (source instanceof FlSequence) {
            return ((FlSequence) source).randomSeedHint();
        }
        return DistributionRandomizer.toSeed(getClass());
    }

    @Override
    public StringBuilder toString(StringBuilder sb) {
        if (source != null) {
            return source.toString(sb);
        }
        return super.toString(sb);
    }
}
