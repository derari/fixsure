package org.cthul.fixsure.generators.value;

import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;
import org.cthul.fixsure.GeneratorException;
import org.cthul.fixsure.base.GeneratorBase;
import org.cthul.fixsure.base.GeneratorTools;
import org.cthul.fixsure.base.SequenceBase;
import org.cthul.fixsure.fluents.FlGeneratorTemplate;
import org.cthul.fixsure.fluents.FlSequence;
import org.hamcrest.Factory;

/**
 *
 * @author Arian Treffer
 */
public abstract class ItemsGenerator<T> 
                extends SequenceBase<T>
                implements FlGeneratorTemplate<T> {
    
    @Factory
    public static <T> FromArray<T> from(T... data) {
        return new FromArray<>(data);
    }

    @Factory
    public static <T> FromIterable<T> from(Iterable<T> data) {
        Class<T> vt = GeneratorTools.typeOf(data);
        return new FromIterable<>(vt, data);
    }

    @Factory
    public static <T> FromList<T> from(List<T> data) {
        Class<T> vt = GeneratorTools.typeOf(data);
        if (data instanceof RandomAccess) {
            return new FromRAList<>(vt, data);
        } else {
            return new FromIterableList<>(vt, data);
        }
    }

    @Factory
    public static <T> FromIterator<T> from(Iterator<T> data) {
        Class<T> vt = GeneratorTools.typeOf(data);
        return new FromIterator<>(vt, data);
    }

    @Factory
    public static <T> FromIterable<T> from(Class<T> valueType, Iterable<T> data) {
        return new FromIterable<>(valueType, data);
    }
    
    @Factory
    public static <T> FromList<T> from(Class<T> valueType, List<T> data) {
        if (data instanceof RandomAccess) {
            return new FromRAList<>(valueType, data);
        } else {
            return new FromIterableList<>(valueType, data);
        }
    }

    @Factory
    public static <T> FromIterator<T> from(Class<T> valueType, Iterator<T> data) {
        return new FromIterator<>(valueType, data);
    }

    public ItemsGenerator() {
    }
    
    @Override
    public abstract ItemsGenerator<T> newGenerator();
    
    public static class FromArray<T> 
                    extends ItemsGenerator<T>
                    implements FlGeneratorTemplate<T> {
        
        private final T[] data;
        private int index;

        public FromArray(T[] data) {
            this(data, 0);
        }

        public FromArray(T[] data, int index) {
            this.data = data;
            this.index = index;
        }

        @Override
        public T next() {
            if (index == data.length) {
                throw new GeneratorException("End of array");
            }
            return data[index++];
        }

        @Override
        public Class<T> getValueType() {
            return (Class) data.getClass().getComponentType();
        }

        @Override
        public FromArray<T> newGenerator() {
            return new FromArray<>(data, index);
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
    
    public static abstract class FromList<T> 
                    extends ItemsGenerator<T>
                    implements FlSequence<T>, FlGeneratorTemplate<T> {
        
        protected final List<? extends T> data;
        protected final Class<T> valueType;
        protected int index;

        public FromList(List<T> data) {
            this(null, data, 0);
        }

        public FromList(List<T> data, int index) {
            this(null, data, index);
        }

        public FromList(Class<T> valueType, List<? extends T> data) {
            this(valueType, data, 0);
        }

        public FromList(Class<T> valueType, List<? extends T> data, int index) {
            this.valueType = valueType;
            this.data = data;
            this.index = index;
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
    
    public static class FromIterableList<T> extends FromList<T> {
        
        private final Iterator<? extends T> it;

        public FromIterableList(List<T> data) {
            super(data);
            it = data.iterator();
        }

        public FromIterableList(List<T> data, int index) {
            super(data, index);
            it = data.iterator();
            gotoIndex(index);
        }

        public FromIterableList(Class<T> valueType, List<? extends T> data) {
            super(valueType, data);
            it = data.iterator();
        }

        public FromIterableList(Class<T> valueType, List<? extends T> data, int index) {
            super(valueType, data, index);
            it = data.iterator();
            gotoIndex(index);
        }

        private void gotoIndex(int index) {
            for (int i = 0; i < index; i++) it.next();
        }

        @Override
        public ItemsGenerator<T> newGenerator() {
            return new FromIterableList<>(valueType, data, index);
        }

        @Override
        public T next() {
            if (!it.hasNext()) {
                throw new GeneratorException("End of iterator");
            }
            index++;
            return it.next();
        }
        
    }
    
    public static class FromRAList<T> extends FromList<T> {

        public FromRAList(List<T> data) {
            super(data);
        }

        public FromRAList(List<T> data, int index) {
            super(data, index);
        }

        public FromRAList(Class<T> valueType, List<? extends T> data) {
            super(valueType, data);
        }

        public FromRAList(Class<T> valueType, List<? extends T> data, int index) {
            super(valueType, data, index);
        }

        @Override
        public ItemsGenerator<T> newGenerator() {
            return new FromRAList<>(valueType, data, index);
        }

        @Override
        public T next() {
            if (index >= data.size()) {
                throw new GeneratorException("End of list");
            }
            int i = index++;
            return data.get(i);
        }
        
    }
    
    public static class FromIterable<T> 
                    extends GeneratorBase<T>
                    implements FlGeneratorTemplate<T> {
        
        private final Iterable<T> data;
        private final Iterator<T> it;
        private final Class<T> valueType;
        private int index;

        public FromIterable(Iterable<T> data) {
            this(null, data, 0);
        }

        public FromIterable(Iterable<T> data, int index) {
            this(null, data, index);
        }

        public FromIterable(Class<T> valueType, Iterable<T> data) {
            this(valueType, data, 0);
        }

        public FromIterable(Class<T> valueType, Iterable<T> data, int index) {
            this.valueType = valueType;
            this.data = data;
            this.it = data.iterator();
            this.index = index;
            for (int i = 0; i < index; i++) it.next();
        }

        @Override
        public T next() {
            if (!it.hasNext()) {
                throw new GeneratorException("End of iterator");
            }
            index++;
            return it.next();
        }

        @Override
        public Class<T> getValueType() {
            if (valueType != null) {
                return valueType;
            }
            return super.getValueType();
        }

        @Override
        public FromIterable<T> newGenerator() {
            return new FromIterable<>(data, index);
        }
    }
    
    public static class FromIterator<T> extends GeneratorBase<T> {
        
        private final Iterator<T> data;
        private final Class<T> valueType;

        public FromIterator(Iterator<T> data) {
            this(null, data);
        }

        public FromIterator(Class<T> valueType, Iterator<T> data) {
            this.data = data;
            this.valueType = valueType;
        }
        
        @Override
        public T next() {
            if (!data.hasNext()) {
                throw new GeneratorException();
            }
            return data.next();
        }
        
        @Override
        public Class<T> getValueType() {
            if (valueType != null) {
                return valueType;
            }
            return super.getValueType();
        }
    }
    
}
