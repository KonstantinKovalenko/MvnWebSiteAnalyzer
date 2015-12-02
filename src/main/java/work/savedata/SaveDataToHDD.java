package work.savedata;

import work.db.Page;
import work.db.Site;
import java.io.*;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import work.analyzer.ExceptionHandler;

@Service("onHDDSaver")
public class SaveDataToHDD {

    @Autowired
    private Site site;
    @Resource(name = "path")
    private PathToSaveOnHDD path;
    @Resource(name = "exceptionHandler")
    private ExceptionHandler exceptionHandler;
    private final String HTTP = "http://";
    private final String HTTPS = "https://";

    private String alignFolderName(String inputString) {
        final int indOfHttp = inputString.indexOf(HTTP);
        final int indOfHttps = inputString.indexOf(HTTPS);
        if (indOfHttps != -1) {
            inputString = inputString.substring(indOfHttps + HTTPS.length());
        } else {
            inputString = inputString.substring(indOfHttp + HTTP.length());
        }
        inputString = inputString.replaceAll("\\W", "-");
        return inputString;
    }

    private void savePageDataToHDD(Page page) {
        String folderName = alignFolderName(page.getPageName());
        String pathToSave = path.getPath() + "\\" + folderName;
        try {
            File f = new File(pathToSave);
            f.mkdirs();
            PrintWriter outPageName = new PrintWriter(new BufferedWriter(new FileWriter(pathToSave + "\\Page URL.txt")));
            PrintWriter outSymbolCounter = new PrintWriter(new BufferedWriter(new FileWriter(pathToSave + "\\Page symbol counter.txt")));
            PrintWriter outMatchesCounter = new PrintWriter(new BufferedWriter(new FileWriter(pathToSave + "\\Page matches counter.txt")));
            try {
                outPageName.println(page.getPageName());
                outSymbolCounter.println(page.getPageSymbolCounter() + " symbols in code this page");
                if (page.getPhraseMatch() != null) {
                    outMatchesCounter.println(page.getPageMatchesCounter() + " instances of the phrase \""
                            + page.getPhraseMatch() + "\" in code this page");
                } else {
                    outMatchesCounter.println("No phrase to seek matches");
                }
            } finally {
                outPageName.close();
                outSymbolCounter.close();
                outMatchesCounter.close();
            }
        } catch (IOException ioe) {
            exceptionHandler.handleException(ioe);
        }
    }

    public void saveData() {
        for (Object obj : site.getSiteDataBase()) {
            savePageDataToHDD((Page) obj);
        }
    }
}
