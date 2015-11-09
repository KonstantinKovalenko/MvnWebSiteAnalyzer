package my.com;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class SaveDataInMySQL implements SaveSiteData {

    private final String mainPage;
    private static ExceptionHandler exceptionHandler;
    private ConnectionProperties cProperties;

    SaveDataInMySQL(ConnectionProperties cProperties,String mainPage) {
        this.mainPage = mainPage;
        createExceptionHandler();
        this.cProperties = cProperties;
    }

    private void createExceptionHandler() {
        WebSiteAnalyzer wsa = new WebSiteAnalyzer("");
        exceptionHandler = wsa.getExceptionHandler();
    }

    @Override
    public void saveData(Site site) {
        Connection con = null;
        try {
            con = openConnection();
            addDataToMainTable(con);
            addDataToSecondaryTable(con, site);
        } catch (SQLException e) {
            exceptionHandler.handleException(e);
        } finally {
            closeConnection(con);
        }
    }

    private void addDataToMainTable(Connection con) throws SQLException {
        final String scanDate = buildScanDate();
        final String sSQL = "insert into sites (MainPage,ScanDate) values ('" + mainPage + "'," + Integer.parseInt(scanDate) + ")";
        Statement statement = con.createStatement();
         statement.execute(sSQL);
    }

    private String buildScanDate() {
        final Calendar c = new GregorianCalendar();
        final String year = Integer.toString(c.get(Calendar.YEAR));
        final String month = Integer.toString(c.get(Calendar.MONTH) + 1);
        final String dayOfMonth = buildDayOfMonth();
        return year + month + dayOfMonth;
    }

    private String buildDayOfMonth() {
        String result = null;
        final Calendar c = new GregorianCalendar();
        final String ZERO = "0";
        final int currentDayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        if (currentDayOfMonth < 10) {
            result = ZERO + Integer.toString(currentDayOfMonth);
        } else {
            result = Integer.toString(currentDayOfMonth);
        }
        return result;
    }

    private void addDataToSecondaryTable(Connection con, Site site) throws SQLException {
        List<Page> siteDB = site.getSiteDataBase();
        for (Page page : siteDB) {
            String sSQL = "insert into data (ID_Sites, PageURL, PhraseMatch,MatchesCounter,SymbolCounter) values "
                    + "((select max(id) from sites),"
                    + "'" + page.getPageName() + "',"
                    + "'" + page.getPhraseMatch() + "',"
                    + page.getPageMatchesCounter() + ","
                    + page.getPageSymbolCounter() + ")";
            Statement statement = con.createStatement();
            statement.execute(sSQL);
        }
    }

    private Connection openConnection() throws SQLException {
        final String dbURL = "jdbc:mysql://" + cProperties.getHost() + ":" + cProperties.getPort() + "/" + cProperties.getDBName();
        return DriverManager.getConnection(dbURL, cProperties.getUserName(), cProperties.getPassword());
    }

    private void closeConnection(Connection con) {
        try {
            con.close();
        } catch (SQLException e) {
            System.err.println("Cannot close connection: " + e.getMessage());
        }
    }
    public static void main (String[] args){
        WebSiteAnalyzer wa = new WebSiteAnalyzer("http://www.beluys.com/create_site/create_site4.html", "html");
        wa.scanWebSite();
        ConnectionProperties cProperties = new ConnectionProperties();
        cProperties.setProperties("localhost","3306","WebSiteAnalyzerDB","root","gosuprotoss");
        wa.saveDataToMySQL(cProperties,wa.getSite());
    }
}
