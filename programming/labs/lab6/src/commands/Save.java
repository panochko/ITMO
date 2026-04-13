package commands;
import com.google.gson.*;
import SEclasses.*;
import design.Color;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serial;
import java.util.Collection;
import java.util.Map;

public final class Save extends AbstractCommand{
    @Serial
    private static final long serialVersionUID = 6653109617298385098L;

    @Override
    public void serverCommand(Collection<Worker> collection, Map<Integer, Worker> mapCollection){
        if (mapCollection == null) {
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("src/server/FileWorker.json"))) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String jsonSet = gson.toJson(collection);
                bufferedWriter.write(jsonSet);
            } catch (IOException e) {
                System.out.println(Color.RED + "такого файла нет");
            }
        }
        if (collection == null) {
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("src/server/FileWorker.json"))) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String jsonSet = gson.toJson(mapCollection);
                bufferedWriter.write(jsonSet);
            } catch (IOException e) {
                System.out.println(Color.RED + "такого файла нет");
            }
        }
    }
}
