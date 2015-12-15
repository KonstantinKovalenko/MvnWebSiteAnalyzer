package work.analyzer;

import org.junit.*;
import static org.junit.Assert.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import work.Configuration;
import work.savedata.SaveDataInMySQL;
import work.savedata.SaveDataToHDD;

public class WebSiteAnalyzerTest {

    private WebSiteAnalyzer wsa;
    private SaveDataInMySQL mySQLSaver;
    private SaveDataToHDD onHDDSaver;

    @Before
    public void setUp() {
        ApplicationContext appContext = new AnnotationConfigApplicationContext(Configuration.class);
        wsa = (WebSiteAnalyzer) appContext.getBean("webSiteAnalyzer");
        mySQLSaver = (SaveDataInMySQL) appContext.getBean("mySQLSaver");
        onHDDSaver = (SaveDataToHDD) appContext.getBean("onHDDSaver");
        wsa.scanWebSite();
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
    public void testSaveDataToHDD() {
        onHDDSaver.saveData();
    }

    @Test
    public void testSaveDataInMySQL() {
        mySQLSaver.saveData();
    }
}
