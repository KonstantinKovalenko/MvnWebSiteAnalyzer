package work.db;

import java.util.*;

public class Site {

    private final List<Page> siteDB;

    public Site() {
        siteDB = new ArrayList();
    }

    public void add(Page page) {
        siteDB.add(page);
    }

    public void clear() {
        siteDB.clear();
    }

    public int size() {
        return siteDB.size();
    }

    public List getSiteDataBase() {
        return siteDB;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Page page : siteDB) {
            result.append(page.toString() + "\n\n");
        }
        return result.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() != siteDB.getClass()) {
            return false;
        }
        ArrayList aList = (ArrayList) o;
        Object[] incArray = aList.toArray();
        Object[] myArray = siteDB.toArray();
        if (myArray.length != incArray.length) {
            return false;
        }
        for (int i = 0; i < myArray.length; i++) {
            if (myArray[i].hashCode() != incArray[i].hashCode()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.siteDB);
        return hash;
    }
}
