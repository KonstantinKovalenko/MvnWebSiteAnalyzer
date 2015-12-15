package work;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import work.analyzer.WebSiteAnalyzer;
import work.db.Site;
import work.savedata.JDBCConnectionProperties;

@ComponentScan(basePackages = "work")
public class Configuration {

    @Autowired
    JDBCConnectionProperties cProperties;

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

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl("jdbc:mysql://"
                + cProperties.getHost() +
                ":" + cProperties.getPort() +
                "/" + cProperties.getDBName());
        dataSource.setUsername(cProperties.getUserName());
        dataSource.setPassword(cProperties.getPassword());
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }
}
