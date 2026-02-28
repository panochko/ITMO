package commands;

import check.ValidatorForCommand;
import design.Color;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

public final class ExecuteScript extends AbstractCommand {
    private static String fileName;
    private final List<String> list = new ArrayList<>();
    public static void avoidRecursion(List<String> list){
        list.add("CommandScript1");
        list.add("CommandScript2");
        list.remove(fileName); //if we remove null from collection, then will happen nothing
        System.out.print(Color.RESET + "В директории src скрипты: ");
        for (String command : list)
            System.out.print(command+" ");
        System.out.println();
    }
    public static void removeAllFrom(List<String> list){
        for (byte i = 0; i < list.size(); i++)
            list.remove(i);
    }
    @Override
    public void execute(String arguments) throws IOException {
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
        avoidRecursion(list);
        String fileName = ValidatorForCommand.checkExecuteScript(scanner, list);
        ExecuteScript.fileName = fileName; // почему нельзя this
        BufferedReader bufferedReader = new BufferedReader(new FileReader("src/" + fileName));
        //list.clear();
        removeAllFrom(list);
        String command;
        while ((command = bufferedReader.readLine()) != null) {
            try {
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