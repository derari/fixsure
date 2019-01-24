package org.cthul.fixsure.api;

/**
 *
 */
public class AbstractStringify implements Stringify {

    @Override
    public String toString() {
        return Stringify.toString(this);
    }

    @Override
    public StringBuilder toString(StringBuilder sb) {
        return sb.append(super.toString());
    }
}
