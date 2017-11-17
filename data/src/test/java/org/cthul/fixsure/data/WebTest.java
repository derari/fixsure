package org.cthul.fixsure.data;

import org.cthul.fixsure.distributions.DistributionRandomizer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 */
public class WebTest {
    
    public WebTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        DistributionRandomizer.setSeed(WebTest.class);
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void test_more_domains() {
        Web.moreDomains().shuffle().many().forEach(System.out::println);
    }
}
