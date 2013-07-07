package org.cthul.fixsure.iterables;

import org.cthul.fixsure.iterables.LazyValues;
import org.cthul.fixsure.Values;
import org.cthul.fixsure.fetchers.TestGenerator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Arian Treffer
 */
public class LazyValuesTest {
    
    public LazyValuesTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testSomeMethod() {
        Values<Number> numbers = LazyValues.<Number>get(3, TestGenerator.gen());//.then(TestGenerator.gen());
    }
}