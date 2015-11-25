package work.analyzer;

import java.io.File;
import org.junit.*;
import static org.junit.Assert.*;
import work.savedata.ConnectionProperties;
import work.savedata.PathToSaveOnHDD;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class WebSiteAnalyzerTest {

    private ApplicationContext appContext;
    private ConnectionProperties cProperties;
    private PathToSaveOnHDD path;

    @Before
    public void setUp() {
        appContext = new ClassPathXmlApplicationContext("config.xml");
        cProperties = appContext.getBean("connProperties", ConnectionProperties.class);
        path = appContext.getBean("path", PathToSaveOnHDD.class);
    }

    @Ignore
    @Test
    public void testCheckURLForValidityTrue() {
        WebSiteAnalyzer wsa = appContext.getBean("WebSiteAnalyzer", WebSiteAnalyzer.class);
        assertTrue(wsa.checkURLForValidity("http://www.beluys.com/create_site/create_site4.html"));
    }

    @Ignore
    @Test
    public void testCheckURLForValidityFalse() {
        WebSiteAnalyzer wsa = new WebSiteAnalyzer("some URL", "html");
        assertFalse(wsa.checkURLForValidity("some URL"));
    }

    @Ignore
    @Test(expected = NullPointerException.class)
    public void testCheckURLForValidityNull() {
        WebSiteAnalyzer wsa = new WebSiteAnalyzer(null, "html");
        wsa.checkURLForValidity(null);
    }

    @Ignore
    @Test
    public void testReturnCleanURLHTTP() {
        WebSiteAnalyzer wsa = new WebSiteAnalyzer("http://www.beluys.com/html_basics/html_page.html");
        String expected = "www.beluys.com";
        assertEquals(expected, wsa.returnCleanURL(wsa.getWebPageURL()));
    }

    @Ignore
    @Test
    public void testReturnCleanURLHTTPS() {
        WebSiteAnalyzer wsa = new WebSiteAnalyzer("https://www.beluys.ru/html_basics/html_page.html");
        String expected = "www.beluys.ru";
        assertEquals(expected, wsa.returnCleanURL(wsa.getWebPageURL()));
    }

    @Ignore
    @Test(expected = IllegalArgumentException.class)
    public void testReturnCleanURLIllegalArgumentEx() {
        WebSiteAnalyzer wsa = new WebSiteAnalyzer("www.beluys.com/html_basics/html_page.html", "html");
        wsa.scanWebPageAndHandleData();
        wsa.returnCleanURL(wsa.getWebPageURL());
    }

    @Ignore
    @Test
    public void testSaveDataToHDD() {
        WebSiteAnalyzer wsa = appContext.getBean("WebSiteAnalyzer", WebSiteAnalyzer.class);
        wsa.scanWebSite();
        wsa.saveDataToHDD(wsa.getSite(), path.getPath());
        File f = new File("d:\\123asd\\www-beluys-com-create_site-create_site4-html");
        assertTrue(f.isDirectory());
    }

    @Test
    public void testSaveDataInMySQL() {
        WebSiteAnalyzer wsa = appContext.getBean("WebSiteAnalyzer", WebSiteAnalyzer.class);
        wsa.scanWebSite();
        wsa.saveDataToMySQL(cProperties, wsa.getSite());
    }
}
