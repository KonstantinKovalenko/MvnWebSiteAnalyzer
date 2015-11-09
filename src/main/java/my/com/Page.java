package my.com;

import java.util.Objects;

public class Page {

    private final int pageSymbolCounter;
    private final int pageMatchesCounter;
    private final String pageName;
    private String phraseMatch;

    Page(String pageName, int pageSymbolCounter, int pageMatchesCounter) {
        this.pageName = pageName;
        this.pageSymbolCounter = pageSymbolCounter;
        this.pageMatchesCounter = pageMatchesCounter;
    }

    Page(String pageName, int pageSymbolCounter, int pageMatchesCounter, String phraseMatch) {
        this.pageName = pageName;
        this.pageSymbolCounter = pageSymbolCounter;
        this.pageMatchesCounter = pageMatchesCounter;
        this.phraseMatch = phraseMatch;
    }

    int getPageSymbolCounter() {
        return pageSymbolCounter;
    }

    int getPageMatchesCounter() {
        return pageMatchesCounter;
    }

    String getPageName() {
        return pageName;
    }

    String getPhraseMatch() {
        return phraseMatch;
    }

    @Override
    public String toString() {
        if (phraseMatch == null) {
            return "Page URL = " + pageName
                    + "\nCount of symbols in page code = " + pageSymbolCounter
                    + "\nThere is no phrase to seek matches";
        } else {
            return "Page URL = " + pageName
                    + "\nCount of symbols in page code = " + pageSymbolCounter
                    + "\nPhrase to seek matches is \"" + phraseMatch + "\""
                    + "\nCount of matches in page code = " + pageMatchesCounter;
        }
    }

    @Override
    public boolean equals(Object o) {
        return this.hashCode() == o.hashCode();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.pageSymbolCounter;
        hash = 89 * hash + this.pageMatchesCounter;
        hash = 89 * hash + Objects.hashCode(this.pageName);
        hash = 89 * hash + Objects.hashCode(this.phraseMatch);
        return hash;
    }
}
