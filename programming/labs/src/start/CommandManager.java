package start;
import commands.*;
import SEclasses.*;

import java.io.IOException;
import java.util.Scanner;
import design.Color;

public class CommandManager {
    public static void run(){
        Info info = new Info();
        Show show = new Show();
        Add add = new Add();
        Update update_id = new Update();
        RemoveByID removeByID = new RemoveByID();
        Clear clear = new Clear();
        Help help = new Help();
        Save save = new Save();
        ExecuteScript executeScript = new ExecuteScript();
        Exit exit = new Exit();
        AddIfMax addIfMax = new AddIfMax();
        AddIfMin addIfMin = new AddIfMin();
        RemoveGreater removeGreater = new RemoveGreater();
        MinByCoordinates minByCoordinates = new MinByCoordinates();
        FilterContainsName filterContainsName = new FilterContainsName();
        FilterStartsWithName filterStartsWithName = new FilterStartsWithName();
        help.execute("");
        while (true) {
            try {
                Scanner scanner = new Scanner(System.in);
                System.out.print(Color.RESET + "=> ");
                String command = scanner.nextLine().trim();
                switch (command) {
                    case "info" -> info.execute("");
                    case "show" -> show.execute("");
                    case "add worker" -> add.execute("");
                    case "update id worker" -> update_id.execute("");
                    case "remove_by_id id" -> removeByID.execute("");
                    case "clear" -> clear.execute("");
                    case "save" -> save.execute(""); //
                    case "execute_script file_name" -> executeScript.execute("");
                    case "exit" -> exit.execute("");
                    case "add_if_max worker" -> addIfMax.execute("");
                    case "add_if_min worker" -> addIfMin.execute("");
                    case "remove_greater worker(s)" -> removeGreater.execute("");
                    case "min_by_coordinates" -> minByCoordinates.execute("");
                    case "filter_contains_name name" -> filterContainsName.execute("");
                    case "filter_starts_with_name name" -> filterStartsWithName.execute("");
                    default -> throw new IOException();
                }
            } catch (IOException e) {
                System.out.println(Color.RED + "такой команды нет");
            }
        }
    }
}
