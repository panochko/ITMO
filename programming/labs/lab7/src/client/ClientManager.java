package client;

import SEclasses.Worker;
import commands.*;
import design.Color;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
        System.out.print("введите ISU пользователя: ");
        String username = scanner.nextLine().trim();
        if (!isAuthorized(username))
            throw new RuntimeException("такой пользователь не авторизован");
        System.out.print("введите пароль пользователя: ");
        String password = scanner.nextLine().trim();
        String[] userArray = {username, password};
        System.out.println(Color.RESET + "help : вывести справку по доступным командам");
        System.out.print(Color.RESET + "=> ");
        String commandHelp = scanner.nextLine().trim().toLowerCase();
        while (!commandHelp.equals("help")) {
            System.out.println("некорректный ввод, введите help");
            System.out.print("=> ");
            commandHelp = scanner.nextLine().trim().toLowerCase();
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
                    Container<FilterContainsName> container = Container.withStringList(filterContainsName, list, userArray);
                    Request.request(container);
                }
                else if (command.matches("filter_starts_with_name [A-Za-zА-Яа-я ,-.]+")) {
                    list.clear();
                    String[] substring = command.split(" ", 2);
                    list.add(substring[1]);
                    FilterStartsWithName filterStartsWithName = (FilterStartsWithName) commandMap.get(substring[0]);
                    filterStartsWithName.setCommandName(command);
                    Container<FilterStartsWithName> container = Container.withStringList(filterStartsWithName, list, userArray);
                    Request.request(container);
                }
                else if (command.matches("remove_by_id [0-9]+")) {
                    integerList.clear();
                    String[] substring = command.split(" ", 2);
                    integerList.add(Integer.parseInt(substring[1]));
                    RemoveByID removeByID = (RemoveByID) commandMap.get(substring[0]);
                    removeByID.setCommandName(command);
                    Container<RemoveByID> container = Container.withIntegerList(removeByID, integerList, userArray);
                    Request.request(container);
                }
                else if (command.matches("update [0-9]+")) {
                    String[] parts = command.split(" ", 2);
                    int id = Integer.parseInt(parts[1]);
                    Update update = (Update) commandMap.get("update");
                    update.setCommandName(command);
                    Worker updatedWorker = update.executeWithIntAndReturn(id, null, null);
                    if (updatedWorker == null) {
                        System.out.println(Color.GREY + "обновление не выполнено (ошибка ввода)");
                        break;
                    }
                    List<Integer> idList = List.of(id);
                    Container<Update> container = Container.withWorkerAndIntegerList(update, updatedWorker, idList, userArray);
                    Request.request(container);
                }
                else if (command.matches("remove_greater [0-9]+")) {
                    integerList.clear();
                    String[] substring = command.split(" ", 2);
                    integerList.add(Integer.parseInt(substring[1]));
                    RemoveGreater removeGreater = (RemoveGreater) commandMap.get(substring[0]);
                    removeGreater.setCommandName(command);
                    Container<RemoveGreater> container = Container.withIntegerList(removeGreater, integerList, userArray);
                    Request.request(container);
                }
                else switch (command) {
                        case "add" -> {
                            Add add = (Add) commandMap.get(command);
                            Worker worker = add.executeWithReturn();
                            Container<Add> container = new Container<>(add, worker, userArray);
                            Request.request(container);
                        }
                        case "add_if_max" -> {
                            AddIfMax addIfMax = (AddIfMax) commandMap.get(command);
                            Worker worker = addIfMax.executeWithReturn();
                            Container<AddIfMax> container = new Container<>(addIfMax, worker, userArray);
                            Request.request(container);
                        }
                        case "add_if_min" -> {
                            AddIfMin addIfMin = (AddIfMin) commandMap.get(command);
                            Worker worker = addIfMin.executeWithReturn();
                            Container<AddIfMin> container = new Container<>(addIfMin, worker, userArray);
                            Request.request(container);
                        }
                        case "info" -> {
                            Info info = (Info) commandMap.get(command);
                            Container<Info> container = new Container<>(info, userArray);
                            Request.request(container);
                        }
                        case "show" -> {
                            Show show = (Show) commandMap.get(command);
                            Container<Show> container = new Container<>(show, userArray);
                            Request.request(container);
                        }
                        case "clear" -> {
                            Clear clear = (Clear) commandMap.get(command);
                            Container<Clear> container = new Container<>(clear, userArray);
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
                            Container<MinByCoordinates> container = new Container<>(minByCoordinates, userArray);
                            Request.request(container);
                        }
                        case "execute_script" -> {
                            ExecuteScript executeScript = (ExecuteScript) commandMap.get(command);
                            executeScript.executeScript();
                            Container<ExecuteScript> container = Container.withStringList(executeScript, executeScript.getStringList(), userArray);
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
    private static boolean isAuthorized(String username) {
        return username.matches("s[0-9]{6}");
    }
}