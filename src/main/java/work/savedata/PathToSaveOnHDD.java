package work.savedata;

import org.springframework.stereotype.Component;

@Component("path")
public class PathToSaveOnHDD {

    private String path = "testneeds";

    public String getPath() {
        return path;
    }
}
