package client;

import SEclasses.Worker;
import commands.*;
import design.Color;
import java.io.IOException;
import java.util.*;

public class ClientManager {
    public static void makeRequest() throws IOException {
        Map<String, AbstractCommand> commandMap = new HashMap<>();
        commandMap.put("help", new Help("help"));
        commandMap.put("add", new Add("add"));
        commandMap.put("add_if_max", new AddIfMax("add_if_max"));
        commandMap.put("add_if_min", new AddIfMin("add_if_min"));
        commandMap.put("filter_contains_name", new FilterContainsName("filter_contains_name"));
        commandMap.put("filter_starts_with_name", new FilterStartsWithName("filter_starts_with_name"));
        commandMap.put("remove_greater", new RemoveGreater("remove_greater"));
        commandMap.put("remove_by_id", new RemoveByID("remove_by_id"));
        commandMap.put("info", new Info("info"));
        commandMap.put("show", new Show("show"));
        commandMap.put("min_by_coordinates", new MinByCoordinates("min_by_coordinates"));
        commandMap.put("exit", new Exit("exit"));
        commandMap.put("update", new Update("update"));
        commandMap.put("clear", new Clear("clear"));
        commandMap.put("execute_script", new ExecuteScript("execute_script"));
        Scanner scanner = new Scanner(System.in);
        System.out.println(Color.RESET + "help : вывести справку по доступным командам");
        System.out.print(Color.RESET + "=> ");
        String commandHelp = scanner.nextLine().trim();
        while (!Objects.equals(commandHelp, "help")) {
            System.out.println("некорректный ввод, введите help");
            System.out.print(Color.RESET + "=> ");
            commandHelp = scanner.nextLine().trim();
        }
        Help help = (Help) commandMap.get(commandHelp);
        Container<Help> helpContainer = new Container<>(help);
        Request.request(helpContainer);
        List<String> list = new ArrayList<>();
        List<Integer> integerList = new ArrayList<>();
        while (true) {
            System.out.print(Color.RESET + "=> ");
            String command = scanner.nextLine().trim();
            try {
                if (command.matches("filter_contains_name [A-Za-zА-Яа-я ,-.]+")) {
                    list.clear();
                    String[] substring = command.split(" ", 2);
                    list.add(substring[1]);
                    FilterContainsName filterContainsName = (FilterContainsName) commandMap.get(substring[0]);
                    filterContainsName.setCommandName(command);
                    Container<FilterContainsName> container = Container.withStringList(filterContainsName, list);
                    Request.request(container);
                }
                else if (command.matches("filter_starts_with_name [A-Za-zА-Яа-я ,-.]+")) {
                    list.clear();
                    String[] substring = command.split(" ", 2);
                    list.add(substring[1]);
                    FilterStartsWithName filterStartsWithName = (FilterStartsWithName) commandMap.get(substring[0]);
                    filterStartsWithName.setCommandName(command);
                    Container<FilterStartsWithName> container = Container.withStringList(filterStartsWithName, list);
                    Request.request(container);
                }
                else if (command.matches("remove_by_id [0-9]+")) {
                    integerList.clear();
                    String[] substring = command.split(" ", 2);
                    integerList.add(Integer.parseInt(substring[1]));
                    RemoveByID removeByID = (RemoveByID) commandMap.get(substring[0]);
                    removeByID.setCommandName(command);
                    Container<RemoveByID> container = Container.withIntegerList(removeByID, integerList);
                    Request.request(container);
                }
                else if (command.matches("update [0-9]+")) {
                    integerList.clear();
                    String[] substring = command.split(" ", 2);
                    integerList.add(Integer.parseInt(substring[1]));
                    Update update = (Update) commandMap.get(substring[0]);
                    update.setCommandName(command);
                    Container<Update> container = Container.withIntegerList(update, integerList);
                    Request.request(container);
                }
                else if (command.matches("remove_greater [0-9]+")) {
                    integerList.clear();
                    String[] substring = command.split(" ", 2);
                    integerList.add(Integer.parseInt(substring[1]));
                    RemoveGreater removeGreater = (RemoveGreater) commandMap.get(substring[0]);
                    removeGreater.setCommandName(command);
                    Container<RemoveGreater> container = Container.withIntegerList(removeGreater, integerList);
                    Request.request(container);
                }
                else switch (command) {
                        case "add" -> {
                            Add add = (Add) commandMap.get(command);
                            Worker worker = add.executeWithReturn();
                            Container<Add> container = new Container<>(add, worker);
                            Request.request(container);
                        }
                        case "add_if_max" -> {
                            AddIfMax addIfMax = (AddIfMax) commandMap.get(command);
                            Worker worker = addIfMax.executeWithReturn();
                            Container<AddIfMax> container = new Container<>(addIfMax, worker);
                            Request.request(container);
                        }
                        case "add_if_min" -> {
                            AddIfMin addIfMin = (AddIfMin) commandMap.get(command);
                            Worker worker = addIfMin.executeWithReturn();
                            Container<AddIfMin> container = new Container<>(addIfMin, worker);
                            Request.request(container);
                        }
                        case "info" -> {
                            Info info = (Info) commandMap.get(command);
                            Container<Info> container = new Container<>(info);
                            Request.request(container);
                        }
                        case "show" -> {
                            Show show = (Show) commandMap.get(command);
                            Container<Show> container = new Container<>(show);
                            Request.request(container);
                        }
                        case "clear" -> {
                            Clear clear = (Clear) commandMap.get(command);
                            Container<Clear> container = new Container<>(clear);
                            Request.request(container);
                        }
                        case "exit" -> {
                            Exit exit = (Exit) commandMap.get(command);
                            Container<Exit> container = new Container<>(exit);
                            Request.request(container);
                            exit.exit();
                        }
                        case "help" -> {
                            Container<Help> container = new Container<>(new Help("help"));
                            Request.request(container);
                        }
                        case "min_by_coordinates" -> {
                            MinByCoordinates minByCoordinates = (MinByCoordinates) commandMap.get(command);
                            Container<MinByCoordinates> container = new Container<>(minByCoordinates);
                            Request.request(container);
                        }
                        case "execute_script" -> {
                            ExecuteScript executeScript = (ExecuteScript) commandMap.get(command);
                            executeScript.executeScript();
                            Container<ExecuteScript> container = Container.withStringList(executeScript, executeScript.getStringList());
                            Request.request(container);
                        }
                        case "filter_contains_name", "filter_starts_with_name" -> System.out.println("команде " + command + " необходимо ввести подстроку");
                        case "remove_by_id", "update", "remove_greater" -> System.out.println("команде " + command + " необходимо ввести число");
                        case "" -> System.out.println("вы не ввели команду");
                        default -> throw new IOException();
                    }
            } catch (IOException e) {
                System.out.println("неизвестная команда: '" + command + "'");
            }
        }
    }
}