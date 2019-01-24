package org.cthul.fixsure.data;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import org.cthul.fixsure.Fixsure;
import org.cthul.fixsure.fluents.FlGenerator;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 */
public class UrlGeneratorTest {

    @Test
    public void test_only_path() {
        FlGenerator<String> urls = UrlGenerator.urls()
                .isAbsolute(false)
                .paths(Fixsure.sequence("a", "b/1"))
                .newGenerator();
        assertThat(urls.next(), is("a"));
        assertThat(urls.next(), is("b/1"));
    }

    @Test
    public void test_only_path_from_array() {
        FlGenerator<String> urls = UrlGenerator.urls()
                .isAbsolute(false)
                .paths("a", "b", "c", "d", "e")
                .seed(2)
                .newGenerator();
        // pseudo-random order
        assertThat(urls.next(), is("c"));
        assertThat(urls.next(), is("d"));
        assertThat(urls.next(), is("b"));
    }
    
    @Test
    public void test_only_path_segments() {
        FlGenerator<String> urls = UrlGenerator.urls()
                .isAbsolute(false)
                .segments("a", "b", "c", "d", "e")
                .segmentCount(1, 3, 7)
                .segmentExts(".a", ".b", ".c")
                .seed(2)
                .newGenerator();
        // pseudo-random composition
        assertThat(urls.next(), is("c/c/c.c"));
        assertThat(urls.next(), is("d/a/b/d/c/c/c.a"));
        assertThat(urls.next(), is("a.a"));
        
    }
    
    @Test
    public void test_full_url() {
        FlGenerator<String> urls = UrlGenerator.urls()
                .isAbsolute(0.75)
                .hasAuthority(0.5)
                .hasProtocol(0.5)
                .hasPort(0.5)
                .hasPath(0.75)
                .seed(2)
                .newGenerator();
        
        // pseudo-random composition
        assertThat(urls.next(), is("44330.html"));
        assertThat(urls.next(), is("/turnip/artichoke/80c0.html"));
        assertThat(urls.next(), is("http://example.com:64649/22f2/radish/24538.html"));
        assertThat(urls.next(), is("/ullucus/61687.html"));
        assertThat(urls.next(), is("//example.net/quinoa.mp4"));
    }
    
    @Test
    public void test_toURLs() throws MalformedURLException {
        URL context = new URL("http://example.com");
        FlGenerator<URL> urls = UrlGenerator.urls()
                .isAbsolute(0.75)
                .hasAuthority(0.5)
                .hasProtocol(0.5)
                .hasPort(0.5)
                .hasPath(0.75) 
                .toURLs(context)
                .newGenerator();
        urls.next(100);
    }
    
    @Test
    public void test_toURLs_no_context() {
        FlGenerator<URL> urls = UrlGenerator.urls()
                .hasPort(0.5)
                .hasPath(0.75) 
                .toURLs()
                .newGenerator();
        urls.next(100);
    }
    
    @Test
    public void test_toURIs() {
        FlGenerator<URI> uris = UrlGenerator.urls()
                .isAbsolute(0.75)
                .hasAuthority(0.5)
                .hasProtocol(0.5)
                .hasPort(0.5)
                .hasPath(0.75) 
                .toURIs()
                .newGenerator();
        uris.next(100);
    }
    
    @Test
    public void test_toPaths() {
        FlGenerator<Path> paths = UrlGenerator.urls()
                .protocol("file")
                .hasPort(false)
                .toPaths()
                .newGenerator();
        paths.next(100);
    }
}
