package org.cthul.fixsure.data;

import java.util.List;
import org.cthul.fixsure.distributions.DistributionRandomizer;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
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
        List<String> domains = Web.moreDomains().shuffle().many();
        assertThat(domains.get(0), is("vanilla57232.example.net"));
        assertThat(domains.get(1), is("cherry34363.example.net"));
        assertThat(domains.get(2), is("peach91103.example.com"));
    }
}
