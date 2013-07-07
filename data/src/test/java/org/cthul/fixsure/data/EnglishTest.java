package org.cthul.fixsure.data;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
/**
 *
 * @author at
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
        String name = English.firstNames().next();
        assertThat(name, not(isEmptyString()));
//        InputStream is = English.get();
//        BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
//        String s;
//        while ((s = br.readLine()) != null) {
//            System.out.println(s);
//        }
    }
}