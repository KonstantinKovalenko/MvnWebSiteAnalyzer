package my.com;

public class ConnectionProperties {

    private String host = null;
    private String port = null;
    private String dbName = null;
    private String userName = null;
    private String password = null;

    public void setProperties(String host, String port, String dbName, String userName, String password) {
        this.host = host;
        this.port = port;
        this.dbName = dbName;
        this.userName = userName;
        this.password = password;
    }

    String getHost() {
        return host;
    }

    String getPort() {
        return port;
    }

    String getDBName() {
        return dbName;
    }

    String getUserName() {
        return userName;
    }

    String getPassword() {
        return password;
    }
}
