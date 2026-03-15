package start;
import commands.*;

import java.io.IOException;
import java.util.*;

import design.Color;

public class CommandManager {
    public static void run(String nameOfCollection) throws IOException {
        switch (nameOfCollection) {
            case "TreeSet" -> AbstractCommand.collection = new TreeSet<>();
            case "ArrayList" -> AbstractCommand.collection = new ArrayList<>();
            case "PriorityQueue" -> AbstractCommand.collection = new PriorityQueue<>();
            default -> AbstractCommand.mapCollection = new LinkedHashMap<>();
        }
        Scanner scanner = new Scanner(System.in);
        Info info = new Info();
        Show show = new Show();
        Add add = new Add();
        Update update_id = new Update();
        RemoveByID removeByID = new RemoveByID();
        Clear clear = new Clear();
        Help help = new Help();
        Save save = new Save();
        Exit exit = new Exit();
        AddIfMax addIfMax = new AddIfMax();
        AddIfMin addIfMin = new AddIfMin();
        RemoveGreater removeGreater = new RemoveGreater();
        MinByCoordinates minByCoordinates = new MinByCoordinates();
        FilterContainsName filterContainsName = new FilterContainsName();
        FilterStartsWithName filterStartsWithName = new FilterStartsWithName();
        ExecuteScript executeScript = new ExecuteScript();
        Loading loader = new Loading();
        switch (nameOfCollection) {
            case "TreeSet" -> loader.execute("TreeSet");
            case "ArrayList" -> loader.execute("ArrayList");
            case "PriorityQueue" -> loader.execute("PriorityQueue");
            default -> loader.execute("LinkedHashMap");
        }
        help.execute("");
        while (true) {
            try {
                System.out.print(Color.RESET + "=> ");
                String command = scanner.nextLine().trim();
                switch (command) {
                    case "info" -> info.execute(nameOfCollection);
                    case "show" -> show.execute(nameOfCollection);
                    case "add worker" -> add.execute(nameOfCollection);
                    case "update id worker" -> update_id.execute(nameOfCollection);
                    case "remove_by_id id" -> removeByID.execute(nameOfCollection);
                    case "clear" -> clear.execute(nameOfCollection);
                    case "save" -> save.execute(nameOfCollection);
                    case "execute_script file_name" -> executeScript.execute(nameOfCollection);
                    case "exit" -> exit.execute(nameOfCollection);
                    case "add_if_max worker" -> addIfMax.execute(nameOfCollection);
                    case "add_if_min worker" -> addIfMin.execute(nameOfCollection);
                    case "remove_greater worker(s)" -> removeGreater.execute(nameOfCollection);
                    case "min_by_coordinates" -> minByCoordinates.execute(nameOfCollection);
                    case "filter_contains_name name" -> filterContainsName.execute(nameOfCollection);
                    case "filter_starts_with_name name" -> filterStartsWithName.execute(nameOfCollection);
                    default -> throw new IOException();
                }
            } catch (IOException e) {
                System.out.println(Color.RED + "такой команды нет");
            }
        }
    }
}