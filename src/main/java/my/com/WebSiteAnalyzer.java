package my.com;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

public class WebSiteAnalyzer {

    private URL url;
    private HttpURLConnection conn;
    private BufferedReader in;
    private String webPageURL;
    private String pageCodeString;
    private int pageSymbolCounter = 0;
    private int pageMatchesCounter = 0;
    private static String phraseMatch;
    private boolean URLisValid = true;
    private Set<String> tempInnerLinks = new HashSet();
    private static volatile Set<String> haveToVisit = new HashSet();
    private static volatile Set<String> visitedInnerLinks = new HashSet();
    private static ExecutorService exec = Executors.newCachedThreadPool();
    private static Site site = new Site();
    private final String HTTP = "http://";
    private final String HTTPS = "https://";
    private final String HREF = "href=\"";
    private final String SLASH = "/";
    private final String QUOTES = "\"";
    private final char CHAR_SLASH = '/';

    private static ExceptionHandler exceptionHandler = new ExceptionHandler();

    public WebSiteAnalyzer(String webPageURL) {
        this.webPageURL = webPageURL;
    }

    public WebSiteAnalyzer(String webPageURL, String phraseMatch) {
        this.webPageURL = webPageURL;
        this.phraseMatch = phraseMatch;
    }

    public void scanWebSite() {
        scanFirstWebPageAndHandleData();
        while (haveToVisitIsNotEmpty()) {
            handleEachNotVisitedLinkInASeparateThread();
            sleepInSeconds(2);
        }
        exec.shutdown();
    }

    private void scanFirstWebPageAndHandleData() {
        scanWebPageAndHandleData();
    }

    private boolean haveToVisitIsNotEmpty() {
        return !haveToVisit.isEmpty();
    }

    private void sleepInSeconds(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            exceptionHandler.handleException(e);
        }
    }

    private void handleEachNotVisitedLinkInASeparateThread() {
        for (String s : getNotVisitedLinksAndClearHaveToVisit()) {
            final WebSiteAnalyzer wsa = new WebSiteAnalyzer(s);
            Thread t = new Thread() {
                public void run() {
                    wsa.scanWebPageAndHandleData();
                }
            };
            exec.execute(t);
        }
    }

    private synchronized List<String> getNotVisitedLinksAndClearHaveToVisit() {
        ArrayList<String> result = new ArrayList();
        for (String s : haveToVisit) {
            if (!visitedInnerLinks.contains(s)) {
                result.add(s);
                visitedInnerLinks.add(s);
            }
        }
        haveToVisit.clear();
        return result;
    }

    void scanWebPageAndHandleData() {
        if (checkURLForValidity(webPageURL)) {
            scanPageCode();
            savePageDataToSiteDataBase();
            fillHaveToVisitLinksSet();
        }
    }

    boolean checkURLForValidity(String stringURL) {
        exceptionHandler.checkObjectForNullLink(stringURL);
        try {
            url = new URL(stringURL);
        } catch (MalformedURLException e) {
            exceptionHandler.handleException(e);
            URLisValid = false;
            return false;
        }
        if (visitedInnerLinks.isEmpty()) {
            visitedInnerLinks.add(stringURL);
        }
        return true;
    }

    private void savePageDataToSiteDataBase() {
        if (objectIsNotNULL(phraseMatch)) {
            site.add(new Page(webPageURL, pageSymbolCounter, pageMatchesCounter, phraseMatch));
        } else {
            site.add(new Page(webPageURL, pageSymbolCounter, pageMatchesCounter));
        }
    }

    private synchronized void fillHaveToVisitLinksSet() {
        for (String s : tempInnerLinks) {
            if (!visitedInnerLinks.contains(s)) {
                haveToVisit.add(s);
            }
        }
    }

    private void scanPageCode() {
        try {
            conn = (HttpURLConnection) url.openConnection();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while (objectIsNotNULL(pageCodeString = in.readLine())) {
                calculatePageCounters(pageCodeString);
                while (checkFor_HTML_PHP_XML_Matches(pageCodeString)) {
                    extractAndAddLinkToTempSetFrom(pageCodeString);
                    pageCodeString = pageCodeString.substring(pageCodeString.indexOf(HREF) + HREF.length());
                }
            }
        } catch (IOException e) {
            exceptionHandler.handleException(e);
        }
    }

    private void calculatePageCounters(String incPageCodeString) {
        pageSymbolCounter += incPageCodeString.length();
        if (objectIsNotNULL(phraseMatch)) {
            String workPageCodeString = incPageCodeString;
            while (workPageCodeString.contains(phraseMatch)) {
                pageMatchesCounter++;
                workPageCodeString = workPageCodeString.substring(workPageCodeString.indexOf(phraseMatch) + phraseMatch.length());
            }
        }
    }

    private boolean checkFor_HTML_PHP_XML_Matches(String stringToCheck) {
        return (checkStringByPattern(stringToCheck, ".*href=\".*" + returnCleanURL(webPageURL) + ".*\\.html.*")
                || checkStringByPattern(stringToCheck, ".*href=\"\\/?\\w*\\.html.*"))
                || (checkStringByPattern(stringToCheck, ".*href=\".*" + returnCleanURL(webPageURL) + ".*\\.php.*")
                || checkStringByPattern(stringToCheck, ".*href=\"\\/?\\w*\\.php.*"))
                || (checkStringByPattern(stringToCheck, ".*href=\".*" + returnCleanURL(webPageURL) + ".*\\.xml.*")
                || checkStringByPattern(stringToCheck, ".*href=\"\\/?\\w*\\.xml.*"));
    }

    private boolean checkStringByPattern(String stringToCheck, String pattern) {
        Pattern myPattern = Pattern.compile(pattern);
        Matcher myMatcher = myPattern.matcher(stringToCheck);
        return myMatcher.matches();
    }

    String returnCleanURL(String inputString) {
        if (URLisNotValid()) {
            throw new IllegalArgumentException();
        }
        String result = inputString;
        final int indOfHttp = result.indexOf(HTTP);
        final int indOfHttps = result.indexOf(HTTPS);
        final int indOfSlash = result.indexOf(SLASH, (indOfHttp + HTTP.length()));
        final int indOfSlashHttps = result.indexOf(SLASH, (indOfHttps + HTTPS.length()));
        if (indexWasFound(indOfSlashHttps)) {
            if (indexWasFound(indOfHttps)) {
                result = result.substring(indOfHttps + HTTPS.length(), indOfSlashHttps);
                return result;
            } else {
                result = result.substring(indOfHttp + HTTP.length(), indOfSlash);
                return result;
            }
        } else {
            if (indexWasFound(indOfHttps)) {
                result = result.substring(indOfHttps + HTTPS.length());
                return result;
            } else {
                result = result.substring(indOfHttp + HTTP.length());
                return result;
            }
        }
    }

    private void extractAndAddLinkToTempSetFrom(String inputString) {
        String result = inputString;
        if (checkStringByPattern(result, ".*href=\".*" + returnCleanURL(webPageURL) + ".*\\.html.*")
                || checkStringByPattern(result, ".*href=\".*" + returnCleanURL(webPageURL) + ".*\\.php.*")
                || checkStringByPattern(result, ".*href=\".*" + returnCleanURL(webPageURL) + ".*\\.xml.*")) {
            extractAndAddStandartLinkToTempSetFrom(result);
        } else if (checkStringByPattern(result, ".*href=\".*\\.html.*")
                || checkStringByPattern(result, ".*href=\".*\\.php.*")
                || checkStringByPattern(result, ".*href=\".*\\.xml.*")) {
            extractAndAddCustomLinkToTempSetFrom(result);
        }
    }

    //examples of standart link:
    //<href="http://mysite.com" 
    //<href="https://mysite.com/anypage.html" 
    private void extractAndAddStandartLinkToTempSetFrom(String inputString) {
        final int indOfHrefHttp = inputString.indexOf("href=\"http");
        final int indOfQuotes = inputString.indexOf(QUOTES, (indOfHrefHttp + HREF.length()));
        if (indexWasFound(indOfQuotes)) {
            inputString = inputString.substring((indOfHrefHttp + HREF.length()), indOfQuotes);
            if (finalSimpleCheckFor_HTML_PHP_XML_Matches(inputString)) {
                tempInnerLinks.add(inputString);
            }
        }
    }

    //examples of custom link:
    //<href="15.xml" 
    //<href="/browse.php?sdat=110"
    private void extractAndAddCustomLinkToTempSetFrom(String inputString) {

        final int indOfHref = inputString.indexOf(HREF);
        final int indOfQuotes = inputString.indexOf(QUOTES, (indOfHref + HREF.length()));
        if (indexWasNotFound(indOfQuotes)) {
            return;
        }
        inputString = inputString.substring((indOfHref + HREF.length()), indOfQuotes);
        if (!finalSimpleCheckFor_HTML_PHP_XML_Matches(inputString)) {
            return;
        }
        assembleAndAddLinkToTempSet(inputString);
    }

    private boolean finalSimpleCheckFor_HTML_PHP_XML_Matches(String stringToCheck) {
        return checkStringByPattern(stringToCheck, ".*\\.html.*")
                || checkStringByPattern(stringToCheck, ".*\\.php.*")
                || checkStringByPattern(stringToCheck, ".*\\.xml.*");
    }

    private void assembleAndAddLinkToTempSet(String inputString) {
        if (URLisShort()) {
            assembleAndAddLinkWhenURLisShort(inputString);
        } else if (URLisShortPlusSlash()) {
            assembleAndAddLinkWhenURLisShortPlusSlash(inputString);
        } else {
            assembleAndAddLinkWhenURLisLong(inputString);
        }
    }

    private boolean URLisShort() {
        return webPageURL.equals(HTTP + returnCleanURL(webPageURL))
                || webPageURL.equals(HTTPS + returnCleanURL(webPageURL));
    }

    private boolean URLisShortPlusSlash() {
        return webPageURL.equals(HTTP + returnCleanURL(webPageURL) + SLASH)
                || webPageURL.equals(HTTPS + returnCleanURL(webPageURL) + SLASH);
    }

    private void assembleAndAddLinkWhenURLisShort(String inputString) {
        if (inputString.charAt(0) != CHAR_SLASH) {
            tempInnerLinks.add(webPageURL + SLASH + inputString);
        } else {
            tempInnerLinks.add(webPageURL + inputString);
        }
    }

    private void assembleAndAddLinkWhenURLisShortPlusSlash(String inputString) {
        if (inputString.charAt(0) != CHAR_SLASH) {
            tempInnerLinks.add(webPageURL + inputString);
            return;
        }
        if (webPageURL.equals(HTTP + returnCleanURL(webPageURL) + SLASH)) {
            tempInnerLinks.add(HTTP + returnCleanURL(webPageURL) + inputString);
        } else {
            tempInnerLinks.add(HTTPS + returnCleanURL(webPageURL) + inputString);
        }
    }

    private void assembleAndAddLinkWhenURLisLong(String inputString) {
        String handleWPU = webPageURL;
        final int lastIndOfSlash = handleWPU.lastIndexOf(SLASH);
        if (inputString.charAt(0) != CHAR_SLASH) {
            handleWPU = handleWPU.substring(0, lastIndOfSlash + 1);
            tempInnerLinks.add(handleWPU + inputString);
        } else {
            handleWPU = handleWPU.substring(0, lastIndOfSlash);
            tempInnerLinks.add(handleWPU + inputString);
        }
    }

    public void saveDataToHDD(Site site, String path) {
        SaveSiteData ssd = new SaveDataToHDD(path);
        ssd.saveData(site);
    }

    public void saveDataToMySQL(ConnectionProperties cProperties, Site site) {
        SaveSiteData ssd = new SaveDataInMySQL(cProperties, returnCleanURL(webPageURL));
        ssd.saveData(site);
    }

    private boolean objectIsNotNULL(Object o) {
        return o != null;
    }

    private boolean URLisNotValid() {
        return !URLisValid;
    }

    private boolean indexWasFound(int index) {
        return index != -1;
    }

    private boolean indexWasNotFound(int index) {
        return !indexWasFound(index);
    }

    public String getWebPageURL() {
        String result = webPageURL;
        return result;
    }

    public Site getSite() {
        Site result = new Site();
        result = site;
        return result;
    }

    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

}
