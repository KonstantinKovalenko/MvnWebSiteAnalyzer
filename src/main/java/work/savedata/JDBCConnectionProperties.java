package work.savedata;

import org.springframework.stereotype.Component;

@Component("cProperties")
public class JDBCConnectionProperties {

    private final String host = "localhost";
    private final String port = "3306";
    private final String dbName = "WebSiteAnalyzerDB";
    private final String userName = "root";
    private final String password = "gosuprotoss";

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getDBName() {
        return dbName;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
