package work.analyzer;

import java.util.*;

public class ExceptionHandler {

    static ArrayList<Exception> errorLog = new ArrayList();

    public void checkObjectForNullLink(Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
    }

    public void handleException(Exception e) {
        errorLog.add(e);
        System.err.println(e);
    }

    public ArrayList getErrorLog() {
        return errorLog;
    }

}
