package work.analyzer;

import work.savedata.ConnectionProperties;
import java.io.File;
import org.junit.*;
import static org.junit.Assert.*;

public class WebSiteAnalyzerTest {

    @Ignore
    @Test
    public void testCheckURLForValidityTrue() {
        WebSiteAnalyzer wa = new WebSiteAnalyzer("http://www.beluys.com/create_site/create_site4.html", "html");
        assertTrue(wa.checkURLForValidity("http://www.beluys.com/create_site/create_site4.html"));
    }

    @Ignore
    @Test
    public void testCheckURLForValidityFalse() {
        WebSiteAnalyzer wa = new WebSiteAnalyzer("some URL", "html");
        assertFalse(wa.checkURLForValidity("some URL"));
    }

    @Ignore
    @Test(expected = NullPointerException.class)
    public void testCheckURLForValidityNull() {
        WebSiteAnalyzer wa = new WebSiteAnalyzer(null, "html");
        wa.checkURLForValidity(null);
    }

    @Ignore
    @Test
    public void testReturnCleanURLHTTP() {
        WebSiteAnalyzer wa = new WebSiteAnalyzer("http://www.beluys.com/html_basics/html_page.html");
        String expected = "www.beluys.com";
        assertEquals(expected, wa.returnCleanURL(wa.getWebPageURL()));
    }

    @Ignore
    @Test
    public void testReturnCleanURLHTTPS() {
        WebSiteAnalyzer wa = new WebSiteAnalyzer("https://www.beluys.ru/html_basics/html_page.html");
        String expected = "www.beluys.ru";
        assertEquals(expected, wa.returnCleanURL(wa.getWebPageURL()));
    }

    @Ignore
    @Test(expected = IllegalArgumentException.class)
    public void testReturnCleanURLIllegalArgumentEx() {
        WebSiteAnalyzer wa = new WebSiteAnalyzer("www.beluys.com/html_basics/html_page.html", "html");
        wa.scanWebPageAndHandleData();
        wa.returnCleanURL(wa.getWebPageURL());
    }

    @Ignore
    @Test
    public void testSaveDataToHDD() {
        WebSiteAnalyzer wa = new WebSiteAnalyzer("http://www.beluys.com/create_site/create_site4.html", "html");
        wa.scanWebSite();
        wa.saveDataToHDD(wa.getSite(), "d:\\123asd");
        File f = new File("d:\\123asd\\www-beluys-com-create_site-create_site4-html");
        assertTrue(f.isDirectory());
    }

    @Test
    public void testSaveDataInMySQL() {
        WebSiteAnalyzer wa = new WebSiteAnalyzer("http://www.beluys.com/create_site/create_site4.html", "html");
        wa.scanWebSite();
        ConnectionProperties cProperties = new ConnectionProperties("localhost", "3306", "WebSiteAnalyzerDB", "root", "gosuprotoss");
        wa.saveDataToMySQL(cProperties, wa.getSite());
    }
}
