package org.cthul.fixsure.data;

import org.cthul.fixsure.fluents.BiDataSource.Pair;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 */
public class EnglishTest {
    
    public EnglishTest() {
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
    public void test() throws Exception {
        String name = English.firstNames().first();
        assertThat(name, not(isEmptyString()));
        English.maleFirstNames().several().forEach(System.out::println);
        System.out.println("\n\n");
        English.firstNames().sorted().several().forEach(System.out::println);
    }
    
    @Test
    public void test_aliceBob() {
        assertThat(English.aliceBobWithGender().pairs().value( 0), is(new Pair<>("Alice", 'F')));
        assertThat(English.aliceBobWithGender().pairs().value(11), is(new Pair<>("Louis", 'M')));
        assertThat(English.aliceBobWithGender().pairs().value(12), is(new Pair<>("Mallory", 'M')));
        assertThat(English.aliceBobWithGender().pairs().value(13), is(new Pair<>("Nancy", 'F')));
        assertThat(English.aliceBobWithGender().pairs().value(25), is(new Pair<>("Zoe", 'F')));
    }
}