package work.savedata;

import work.db.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import work.analyzer.ExceptionHandler;

@Repository("mySQLSaver")
public class SaveDataInMySQL {

    @Autowired
    private String mainPage;
    @Autowired
    private Site site;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Resource(name = "exceptionHandler")
    private ExceptionHandler exceptionHandler;

    public void saveData() {
        try {
            addDataToMainTable();
            addDataToSecondaryTable();
        } catch (Exception e) {
            exceptionHandler.handleException(e);
        }
    }

    private void addDataToMainTable() {
        final String scanDate = buildScanDate();
        jdbcTemplate.update("insert into sites (MainPage,ScanDate) values ('" + mainPage + "'," + Integer.parseInt(scanDate) + ")");
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

    private void addDataToSecondaryTable() {
        List<Page> siteDB = site.getSiteDataBase();
        for (Page page : siteDB) {
            jdbcTemplate.update("insert into data (ID_Sites, PageURL, PhraseMatch, MatchesCounter, SymbolCounter) values "
                    + "((select max(id) from sites),"
                    + "'" + page.getPageName() + "',"
                    + "'" + page.getPhraseMatch() + "',"
                    + page.getPageMatchesCounter() + ","
                    + page.getPageSymbolCounter() + ")");
        }
    }
}
