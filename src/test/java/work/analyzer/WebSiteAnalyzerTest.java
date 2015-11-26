package work.analyzer;

import work.savedata.ConnectionProperties;
import java.io.File;
import org.junit.*;
import static org.junit.Assert.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import work.Configuration;
import work.savedata.PathToSaveOnHDD;

public class WebSiteAnalyzerTest {

    private ApplicationContext appContext;
    private WebSiteAnalyzer wsa;
    private PathToSaveOnHDD path;
    private ConnectionProperties cProperties;

    @Before
    public void setUp() {
        appContext = new AnnotationConfigApplicationContext(Configuration.class);
        wsa = (WebSiteAnalyzer) appContext.getBean("webSiteAnalyzer");
        path = (PathToSaveOnHDD) appContext.getBean("path");
        cProperties = (ConnectionProperties) appContext.getBean("cProperties");
    }

    @Test
    public void testCheckURLForValidityTrue() {
        assertTrue(wsa.checkURLForValidity("http://www.beluys.com/create_site/create_site4.html"));
    }

    @Test
    public void testCheckURLForValidityFalse() {
        WebSiteAnalyzer wa = new WebSiteAnalyzer("some URL", "html");
        assertFalse(wa.checkURLForValidity("some URL"));
    }

    @Test(expected = NullPointerException.class)
    public void testCheckURLForValidityNull() {
        WebSiteAnalyzer wa = new WebSiteAnalyzer(null, "html");
        wa.checkURLForValidity(null);
    }

    @Test
    public void testReturnCleanURLHTTP() {
        String expected = "www.beluys.com";
        assertEquals(expected, wsa.returnCleanURL(wsa.getWebPageURL()));
    }

    @Test
    public void testReturnCleanURLHTTPS() {
        WebSiteAnalyzer wa = new WebSiteAnalyzer("https://www.beluys.ru/anyother/123abc.html");
        String expected = "www.beluys.ru";
        assertEquals(expected, wa.returnCleanURL(wa.getWebPageURL()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReturnCleanURLIllegalArgumentEx() {
        WebSiteAnalyzer wa = new WebSiteAnalyzer("www.beluys.com/html_basics/html_page.html", "html");
        wa.scanWebPageAndHandleData();
        wa.returnCleanURL(wa.getWebPageURL());
    }

    @Test
    public void testSaveDataToHDD() {
        wsa.scanWebSite();
        wsa.saveDataToHDD(wsa.getSite(), path.getPath());
        File f = new File(path.getPath() + "/www-beluys-com-create_site-create_site4-html");
        assertTrue(f.isDirectory());
    }

    @Test
    public void testSaveDataInMySQL() {
        wsa.scanWebSite();
        wsa.saveDataToMySQL(cProperties, wsa.getSite());
    }
}
