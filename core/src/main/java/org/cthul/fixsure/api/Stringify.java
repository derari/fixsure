package org.cthul.fixsure.api;

import org.cthul.fixsure.generators.GeneratorTools;

/**
 *
 */
public interface Stringify {
    
    default StringBuilder toString(StringBuilder sb) {
        return sb.append(GeneratorTools.lambdaToString(this, sb));
    }
    
    static String toString(Stringify stringify) {
        if (stringify == null) {
            return "null";
        } else {
            return stringify.toString(new StringBuilder()).toString();
        }
    }
    
    static StringBuilder toString(Object o, StringBuilder sb) {
        if (o instanceof Stringify) {
            return ((Stringify) o).toString(sb);
        } else {
            return sb.append(o);
        }
    }
}
