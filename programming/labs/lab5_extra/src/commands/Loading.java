package commands;
import SEclasses.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import design.Color;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Map;

public final class Loading extends AbstractCommand{
    @Override
    public void execute(String arguments) throws IOException {
        File file = new File("src/start/FileWorker.json");
        if (!file.exists() || file.length() == 0) return;

        Gson gson = new Gson();
        String content = new String(Files.readAllBytes(file.toPath())).trim();
        if (content.startsWith("[")) {
            Worker[] workers = gson.fromJson(content, Worker[].class);
            if (arguments.equals("ArrayList") || arguments.equals("TreeSet") || arguments.equals("PriorityQueue"))
                Collections.addAll(collection, workers);
            else {
                for (Worker worker : workers)
                    mapCollection.put(worker.getId(), worker);
            }
        } else if (content.startsWith("{")) {
            Type mapType = new TypeToken<Map<Integer, Worker>>(){}.getType();
            Map<Integer, Worker> loadedMap = gson.fromJson(content, mapType);
            if (arguments.equals("ArrayList") || arguments.equals("TreeSet") || arguments.equals("PriorityQueue"))
                collection.addAll(loadedMap.values());
            else
                mapCollection.putAll(loadedMap);
        } else
            throw new IOException(Color.RED + "неизвестный формат JSON");
    }
}