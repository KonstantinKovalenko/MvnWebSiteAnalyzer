package work;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import work.analyzer.WebSiteAnalyzer;
import work.db.Site;

@ComponentScan(basePackages = "work")
public class Configuration {

    @Bean
    @Scope("prototype")
    public WebSiteAnalyzer webSiteAnalyzer() {
        return new WebSiteAnalyzer("http://www.beluys.com/create_site/create_site4.html", "html");
    }

    @Bean
    public Site site() {
        WebSiteAnalyzer wsa = webSiteAnalyzer();
        return wsa.getSite();
    }

    @Bean
    public String mainPage() {
        WebSiteAnalyzer wsa = webSiteAnalyzer();
        return wsa.getCleanWebPageURL();
    }
}
