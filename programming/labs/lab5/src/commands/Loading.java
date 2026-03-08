package commands;
import SEclasses.*;
import com.google.gson.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;

public final class Loading extends AbstractCommand{
    @Override
    public void execute(String arguments) throws IOException{
        File file = new File("src/start/FileWorker.json");
        Gson gson = new Gson();
        if (file.exists() && file.length() > 0){
            try (FileReader fileReader = new FileReader(file)) {
                Worker[] workers = gson.fromJson(fileReader, Worker[].class);
                Collections.addAll(set, workers);
            }
        }
    }
}