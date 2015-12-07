package work.savedata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.annotation.PostConstruct;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component("path")
public class PathToSaveOnHDD {

    private String path;

    @PostConstruct
    private void init() throws IOException {
        ApplicationContext context = new AnnotationConfigApplicationContext();
        Resource resource = context.getResource("pathToSave.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(resource.getInputStream()));
        path = in.readLine();
    }

    public String getPath() {
        return path;
    }
}
