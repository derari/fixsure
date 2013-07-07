package org.cthul.fixsure.generators.reflection;

import org.cthul.fixsure.Generator;
import org.cthul.fixsure.base.GeneratorBase;
import org.cthul.fixsure.base.GeneratorTools;
import org.cthul.objects.reflection.Signatures;

/**
 *
 */
public abstract class AbstractCallGenerator<T> 
                extends GeneratorBase<T> {
    
    protected static final Object[] NO_ARGS = {};
    protected static final Generator[] NO_GENERATORS = {};
    
    private final Class[] paramsWithVarArgs;
    private final Generator[] argGenerators;

    public AbstractCallGenerator(Generator[] argGenerators, boolean varArgs, Class[] paramTypes) {
        this.paramsWithVarArgs = varArgs ? paramTypes : null;
        this.argGenerators = argGenerators;
    }
    
    protected AbstractCallGenerator(AbstractCallGenerator src) {
        this.argGenerators = src.argGenerators.clone();
        for (int i = 0; i < argGenerators.length; i++) {
            argGenerators[i] = GeneratorTools.newGeneratorFromTemplate(argGenerators[i]);
        }
        this.paramsWithVarArgs = src.paramsWithVarArgs;
    }
    
    protected Object[] nextArgs() {
        if (argGenerators.length == 0) return NO_ARGS;
        Object[] args = new Object[argGenerators.length];
        for (int i = 0; i < argGenerators.length; i++) {
            args[i] = argGenerators[i].next();
        }
        if (paramsWithVarArgs != null) {
            args = Signatures.fixVarArgs(paramsWithVarArgs, args);
        }
        return args;
    }
    
}
