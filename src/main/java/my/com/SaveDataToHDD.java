package my.com;

import java.io.*;

public class SaveDataToHDD implements SaveSiteData {

    private final String path;
    private final String HTTP = "http://";
    private final String HTTPS = "https://";
    private static ExceptionHandler exceptionHandler;

    SaveDataToHDD(String path) {
        this.path = path;
        createExceptionHandler();
    }

    private void createExceptionHandler() {
        WebSiteAnalyzer wsa = new WebSiteAnalyzer("");
        exceptionHandler = wsa.getExceptionHandler();
    }

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

    private void savePageDataToHDD(String path, Page page) {
        String folderName = alignFolderName(page.getPageName());
        String pathToSave = path + "\\" + folderName;
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

    @Override
    public void saveData(Site site) {
        for (Object obj : site.getSiteDataBase()) {
            savePageDataToHDD(path, (Page) obj);
        }
    }
}
