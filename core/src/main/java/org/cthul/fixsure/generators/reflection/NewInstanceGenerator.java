package org.cthul.fixsure.generators.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.base.GeneratorTools;
import org.cthul.fixsure.fluents.FlGeneratorTemplate;
import org.cthul.objects.reflection.Signatures;
import org.hamcrest.Factory;

/**
 *
 */
public class NewInstanceGenerator<T> 
                extends AbstractCallGenerator<T> 
                implements FlGeneratorTemplate<T> {
    
    @Factory
    public static <T> NewInstanceGenerator<T> instancesOf(Class<T> type) {
        return new NewInstanceGenerator<>(type);
    }
    
    @Factory
    public static <T> NewInstanceGenerator<T> instancesOf(Class<T> type, Object... args) {
        return new NewInstanceGenerator<>(type, args);
    }
    
    @Factory
    public static <T> NewInstanceGenerator<T> instancesOf(Class<T> type, Generator... args) {
        return new NewInstanceGenerator<>(type, args);
    }
    
    private final Constructor<T> c;

    public NewInstanceGenerator(Class<T> type) {
        this(type, NO_GENERATORS);
    }
    
    public NewInstanceGenerator(Class<T> type, Object... args) {
        this(type, GeneratorTools.valuesAsGenerators(args));
    }
    
    public NewInstanceGenerator(Class<T> type, Generator... args) {
        this(type, GeneratorTools.typesOf(args), args);
    }
    
    public NewInstanceGenerator(Class<T> type, Class[] argTypes, Generator[] args) {
        this(Signatures.<T>bestConstructor(type, argTypes), args);
    }
    
    public NewInstanceGenerator(Constructor<T> c) {
        this(c, NO_GENERATORS);
    }
    
    public NewInstanceGenerator(Constructor<T> c, Object... args) {
        this(c, GeneratorTools.valuesAsGenerators(args));
    }
    
    public NewInstanceGenerator(Constructor<T> c, Generator... args) {
        super(args, c.isVarArgs(), c.getParameterTypes());
        this.c = c;
    }
    
    protected NewInstanceGenerator(NewInstanceGenerator src) {
        super(src);
        this.c = src.c;
    }

    @Override
    public T next() {
        Object[] args = nextArgs();
        try {
            return (T) c.newInstance(args);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Class<T> getValueType() {
        return (Class) c.getDeclaringClass();
    }

    @Override
    public NewInstanceGenerator<T> newGenerator() {
        return new NewInstanceGenerator<>(this);
    }
    
}
