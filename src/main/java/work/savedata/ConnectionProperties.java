package work.savedata;

public class ConnectionProperties {

    private final String HOST;
    private final String PORT;
    private final String DBNAME;
    private final String USERNAME;
    private final String PASSWORD;

    public ConnectionProperties(String host, String port, String dbName, String userName, String password) {
        HOST = host;
        PORT = port;
        DBNAME = dbName;
        USERNAME = userName;
        PASSWORD = password;
    }

    public String getHost() {
        return HOST;
    }

    public String getPort() {
        return PORT;
    }

    public String getDBName() {
        return DBNAME;
    }

    public String getUserName() {
        return USERNAME;
    }

    public String getPassword() {
        return PASSWORD;
    }
}
