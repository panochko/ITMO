package commands;
import SEclasses.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import design.Color;
import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public final class Loading extends AbstractCommand{
    @Serial
    private static final long serialVersionUID = -1783183273143357466L;

    // on server
    @Override
    public void serverCommand(Collection<Worker> collection, Map<Integer, Worker> mapCollection) throws IOException {
        File file = new File("src/server/FileWorker.json");
        if (!file.exists() || file.length() == 0) return;
        Gson gson = new Gson();
        String content = new String(Files.readAllBytes(file.toPath())).trim();
        if (content.startsWith("[")) {
            Worker[] workers = gson.fromJson(content, Worker[].class);
            if (mapCollection == null)
                Collections.addAll(collection, workers);
            if (collection == null) {
                for (Worker worker : workers)
                    mapCollection.put(worker.getId(), worker);
            }
        } else if (content.startsWith("{")) {
            Type mapType = new TypeToken<Map<Integer, Worker>>(){}.getType();
            Map<Integer, Worker> loadedMap = gson.fromJson(content, mapType);
            if (mapCollection == null)
                collection.addAll(loadedMap.values());
            if (collection == null)
                mapCollection.putAll(loadedMap);
        } else
            throw new IOException(Color.RED + "неизвестный формат JSON");
    }
}