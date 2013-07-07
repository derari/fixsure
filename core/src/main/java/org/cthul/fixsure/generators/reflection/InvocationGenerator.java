package org.cthul.fixsure.generators.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.base.GeneratorTools;
import org.cthul.fixsure.fluents.FlGeneratorTemplate;
import org.cthul.fixsure.generators.value.ConstantGenerator;
import org.cthul.objects.reflection.Signatures;
import org.hamcrest.Factory;

/**
 *
 */
public class InvocationGenerator<T> 
                extends AbstractCallGenerator<T> 
                implements FlGeneratorTemplate<T> {
    
    @Factory
    public static <T> InvocationGenerator<T> invoke(Object instance, String m) {
        return new InvocationGenerator<>(instance, m);
    }
    
    @Factory
    public static <T> InvocationGenerator<T> invoke(Object instance, String m, Object... args) {
        return new InvocationGenerator<>(instance, m, args);
    }
    
    @Factory
    public static <T> InvocationGenerator<T> invoke(Object instance, String m, Generator... args) {
        return new InvocationGenerator<>(instance, m, args);
    }
    
    @Factory
    public static <T> InvocationGenerator<T> invoke(Generator<?> instance, String m) {
        return new InvocationGenerator<>(instance, m);
    }
    
    @Factory
    public static <T> InvocationGenerator<T> invoke(Generator<?> instance, String m, Object... args) {
        return new InvocationGenerator<>(instance, m, args);
    }
    
    @Factory
    public static <T> InvocationGenerator<T> invoke(Generator<?> instance, String m, Generator... args) {
        return new InvocationGenerator<>(instance, m, args);
    }
    
    private final Generator<?> instance;
    private final Method m;

    public InvocationGenerator(Object instance, String m) {
        this(instance, m, NO_GENERATORS);
    }
    
    public InvocationGenerator(Object instance, String m, Object... args) {
        this(instance, m, GeneratorTools.valuesAsGenerators(args));
    }
    
    public InvocationGenerator(Object instance, String m, Generator... args) {
        this(asClass(instance), ConstantGenerator.constant(instance), m, args);
    }
    
    private static Class asClass(Object instance) {
        if (instance instanceof Class) {
            return (Class) instance;
        }
        return instance.getClass();
    }
    
    public InvocationGenerator(Generator<?> instance, String m) {
        this(instance, m, NO_GENERATORS);
    }
    
    public InvocationGenerator(Generator<?> instance, String m, Generator... args) {
        this(GeneratorTools.typeOf(instance), instance, m, args);
    }
    
    public InvocationGenerator(Class<?> type, Generator<?> instance, String m, Generator... args) {
        this(type, instance, m, GeneratorTools.typesOf(args), args);
    }
    
    public InvocationGenerator(Class<?> type, Generator<?> instance, String m, Class[] argTypes, Generator[] args) {
        this(instance,
                Signatures.bestMethod(type, m, argTypes),
                args);
    }
    
    public InvocationGenerator(Object instance, Method m) {
        this(instance, m, NO_GENERATORS);
    }
    
    public InvocationGenerator(Object instance, Method m, Object... args) {
        this(instance, m, GeneratorTools.valuesAsGenerators(args));
    }
    
    public InvocationGenerator(Object instance, Method m, Generator... args) {
        this(ConstantGenerator.constant(instance), m, args);
    }
    
    public InvocationGenerator(Generator<?> instance, Method m, Generator... args) {
        super(args, m.isVarArgs(), m.getParameterTypes());
        this.instance = instance;
        this.m = m;
    }
    
    protected InvocationGenerator(InvocationGenerator src) {
        super(src);
        this.instance = src.instance;
        this.m = src.m;
    }

    @Override
    public T next() {
        Object inst = instance.next();
        Object[] args = nextArgs();
        try {
            return (T) m.invoke(inst, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Class<T> getValueType() {
        return (Class) m.getReturnType();
    }

    @Override
    public InvocationGenerator<T> newGenerator() {
        return new InvocationGenerator<>(this);
    }
    
}
