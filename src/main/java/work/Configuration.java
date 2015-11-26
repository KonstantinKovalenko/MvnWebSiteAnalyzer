package work;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import work.analyzer.WebSiteAnalyzer;
import work.savedata.ConnectionProperties;
import work.savedata.PathToSaveOnHDD;

public class Configuration {

    @Bean
    public ConnectionProperties cProperties() {
        return new ConnectionProperties("localhost", "3306", "WebSiteAnalyzerDB", "root", "gosuprotoss");
    }

    @Bean
    public PathToSaveOnHDD path() {
        return new PathToSaveOnHDD("testneeds");
    }

    @Bean
    @Scope("prototype")
    public WebSiteAnalyzer webSiteAnalyzer() {
        return new WebSiteAnalyzer("http://www.beluys.com/create_site/create_site4.html", "html");
    }
}
