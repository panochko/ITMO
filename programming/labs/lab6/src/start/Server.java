package start;

import commands.AbstractCommand;
import commands.Save;
import design.Color;
import server.ServerManager;
import java.io.IOException;
import static server.ServerManager.logger;

public class Server {
    public static String nameOfCollection;
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println(Color.RED + "доступные коллекции: ArrayList, TreeSet, LinkedHashMap, PriorityQueue");
            return;
        }
        nameOfCollection = args[0];
        if (!nameOfCollection.equals("ArrayList") && !nameOfCollection.equals("TreeSet") && !nameOfCollection.equals("LinkedHashMap") && !nameOfCollection.equals("PriorityQueue")) {
            System.out.println(Color.RED + "доступные коллекции: ArrayList, TreeSet, LinkedHashMap, PriorityQueue");
            return;
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Save save = new Save();
                save.serverCommand(AbstractCommand.collection, AbstractCommand.mapCollection);
                logger.info("Collection saved to file");
            } catch (Exception e) {
                logger.error("Could not save collection: {}", e.getMessage());
            }
        }));
        Thread thread = new Thread(() -> {
            try {
                ServerManager.response(nameOfCollection);
            } catch (IOException e) {
                System.out.println(Color.RED + "ошибка подключения к серверу");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
    }
}