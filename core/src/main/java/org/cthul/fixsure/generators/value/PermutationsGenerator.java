package org.cthul.fixsure.generators.value;

import org.cthul.fixsure.Factory;
import org.cthul.fixsure.GeneratorException;
import org.cthul.fixsure.fluents.FlTemplate;
import org.cthul.fixsure.generators.CopyableGenerator;

public class PermutationsGenerator<T> implements CopyableGenerator<T[]> {
    
    /**
     * Generates permutations of an array
     * @param <T>
     * @param array
     * @return permutations generator
     */
    @Factory
    public static <T> FlTemplate<T[]> permutations(T... array) {
        T[] clone = array.clone();
        return () -> new PermutationsGenerator<>(clone);
    }
    
    private static final int DIRECTION_MASK = 3;
    private static final int LEFT = -1;
    private static final int STOP =  0;
    private static final int RIGHT = 1;
    
    private final T[] array;
    private final int[] directions;
    private boolean beforeFirst = true;

    public PermutationsGenerator(T[] array) {
        this.array = array.clone();
        this.directions = new int[array.length];
        initDirections();
    }
    
    protected PermutationsGenerator(PermutationsGenerator<T> src) {
        this.array = src.array.clone();
        this.directions = src.directions.clone();
        this.beforeFirst = src.beforeFirst;
    }

    @Override
    public PermutationsGenerator<T> copy() {
        return new PermutationsGenerator<>(this);
    }
    
    @Override
    public Class<T[]> getValueType() {
        return (Class) array.getClass();
    }

    private void initDirections() {
        if (directions.length > 0) {
            directions[0] = setDirection(0, STOP);
            for (int i = 1; i < directions.length; i++) {
                directions[i] = setDirection(i << 2, LEFT);
            }
        }
    }
    
    private int getDirection(int value) {
        int d = value & DIRECTION_MASK;
        return d == 3 ? -1 : d;
    }
    
    private int setDirection(int value, int direction) {
        return (value & ~DIRECTION_MASK) | (direction & DIRECTION_MASK);
    }
    
    private void swap(int a, int b) {
        int d = directions[a];
        directions[a] = directions[b];
        directions[b] = d;
        T t = array[a];
        array[a] = array[b];
        array[b] = t;
    }
    
    @Override
    public T[] next() {
        if (beforeFirst) {
            beforeFirst = false;
        } else {
            nextPermutation();
        }
        return array.clone();
    }

    protected void nextPermutation() {
        // find highest moving element
        int index = -1, value = -1, dir = 0;
        for (int i = 0; i < directions.length; i++) {
            int v = directions[i];
            if (v > value) {
                int d = getDirection(v);
                if (d != STOP) {
                    index = i;
                    value = v;
                    dir = d;
                }
            }
        }
        if (index < 0) {
            // nothing moves
            throw new GeneratorException("No more permutations");
        }
        // move the element
        final int newIndex = index + dir;
        swap(index, newIndex);
        if (newIndex == 0 
                || newIndex+1 == directions.length 
                || directions[newIndex + dir] > value) {
            // if element is at border, or next element is bigger, 
            // stop current element
            directions[newIndex] = setDirection(value, STOP);
        }
        // activate unbounded elements bigger than current
        for (int i = 0; i < newIndex; i++) {
            int v = directions[i];
            if (v > value) {
                directions[i] = setDirection(v, RIGHT);
            }
        }
        for (int i = newIndex+1; i < directions.length; i++) {
            int v = directions[i];
            if (v > value) {
                directions[i] = setDirection(v, LEFT);
            }
        }
    }
}
