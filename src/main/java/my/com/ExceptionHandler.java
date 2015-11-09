package my.com;

import java.util.*;

public class ExceptionHandler {

    static ArrayList<Exception> errorLog = new ArrayList();

    void checkObjectForNullLink(Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
    }

    void handleException(Exception e) {
        errorLog.add(e);
        System.err.println(e);
    }

    ArrayList getErrorLog() {
        return errorLog;
    }

}
