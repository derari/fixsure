package org.cthul.fixsure.generators.value;

import java.util.*;
import org.cthul.fixsure.Factory;
import org.cthul.fixsure.fluents.FlGenerator;
import org.cthul.fixsure.generators.BoundedSequence;
import org.cthul.fixsure.generators.GeneratorTools;

/**
 *
 */
public abstract class ItemsSequence<T> extends BoundedSequence<T> {
    
    /**
     * Converts an array into a sequence.
     * @param <T>
     * @param data
     * @return sequence
     */
    @Factory
    public static <T> ItemsSequence<T> sequence(T... data) {
        return new FromArray<>(data);
    }

////    public static <T> FromIterable<T> from(Iterable<T> data) {
//        Class<T> vt = GeneratorTools.typeOf(data);
//        return new FromIterable<>(vt, data);
//    }

    /**
     * Converts a collection into a sequence.
     * @param <T>
     * @param data
     * @return sequence
     */
    @Factory
    public static <T> ItemsSequence<T> sequence(Collection<T> data) {
        Class<T> vt = GeneratorTools.typeOf(data);
        return sequence(vt, data);
    }

    /**
     * Converts a collection into a sequence.
     * @param <T>
     * @param valueType
     * @param data
     * @return sequence
     */
    @Factory
    public static <T> ItemsSequence<T> sequence(Class<T> valueType, Collection<T> data) {
        if (data instanceof List && data instanceof RandomAccess) {
            return new FromRAList<>(valueType, (List) data);
        } else {
            return new FromCollection<>(valueType, data);
        }
    }
//
////    public static <T> FromIterator<T> from(Iterator<T> data) {
//        Class<T> vt = GeneratorTools.typeOf(data);
//        return new FromIterator<>(vt, data);
//    }
//
////    public static <T> FromIterable<T> from(Class<T> valueType, Iterable<T> data) {
//        return new FromIterable<>(valueType, data);
//    }
//    
////    public static <T> FromList<T> from(Class<T> valueType, List<T> data) {
//        if (data instanceof RandomAccess) {
//            return new FromRAList<>(valueType, data);
//        } else {
//            return new FromIterableList<>(valueType, data);
//        }
//    }
//
////    public static <T> FromIterator<T> from(Class<T> valueType, Iterator<T> data) {
//        return new FromIterator<>(valueType, data);
//    }

    public ItemsSequence() {
    }
    
    public static class FromArray<T> extends ItemsSequence<T> {
        
        private final T[] data;

        public FromArray(T[] data) {
            this.data = data;
        }

        @Override
        public Class<T> getValueType() {
            return (Class) data.getClass().getComponentType();
        }

        @Override
        public T value(long n) {
            return data[(int) n];
        }

        @Override
        public long length() {
            return data.length;
        }
        
    }
    
    public static class FromRAList<T> extends ItemsSequence<T> {
        
        private final List<? extends T> data;
        private final Class<T> valueType;

        public FromRAList(List<? extends T> data) {
            this(null, data);
        }

        public FromRAList(Class<T> valueType, List<? extends T> data) {
            this.valueType = valueType;
            this.data = data;
        }

        @Override
        public Class<T> getValueType() {
            if (valueType != null) {
                return valueType;
            }
            return super.getValueType();
        }

        @Override
        public T value(long n) {
            return data.get((int) n);
        }

        @Override
        public long length() {
            return data.size();
        }
    }
    
    public static class FromCollection<T> extends ItemsSequence<T> {
        
        private final Collection<T> data;
        private final Class<T> valueType;
        private Iterator<? extends T> it;
        private long position;

        public FromCollection(Class<T> valueType, Collection<T> data) {
            this.valueType = valueType;
            this.data = data;
            it = data.iterator();
            position = 0;
        }

        private void gotoIndex(long index) {
            if (position > index) {
                position = 0;
                it = data.iterator();
            }
            for (; position < index; position++) it.next();
        }
        
        private FlGenerator<T> superNewGenerator() {
            return super.newGenerator();
        }

        @Override
        public FlGenerator<T> newGenerator() {
            return new FromCollection<>(valueType, data).superNewGenerator();
        }

        @Override
        public T value(long n) {
            gotoIndex(n);
            position++;
            return it.next();
        }

        @Override
        public long length() {
            return data.size();
        }        
    }
}
