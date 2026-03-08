package commands;

import SEclasses.*;
import check.ValidatorForCommand;
import design.Color;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.Scanner;
import java.util.TreeSet;

public final class ExecuteScript extends AbstractCommand {
    private final Set<String> list = new TreeSet<>();

    Scanner scanner = new Scanner(System.in);
    Info info = new Info();
    Show show = new Show();
    Add add = new Add();
    Update update_id = new Update();
    RemoveByID removeByID = new RemoveByID();
    Clear clear = new Clear();
    Save save = new Save();
    Exit exit = new Exit();
    AddIfMax addIfMax = new AddIfMax();
    AddIfMin addIfMin = new AddIfMin();
    RemoveGreater removeGreater = new RemoveGreater();
    MinByCoordinates minByCoordinates = new MinByCoordinates();
    FilterContainsName filterContainsName = new FilterContainsName();
    FilterStartsWithName filterStartsWithName = new FilterStartsWithName();

    private void findScripts(Set<String> set){
        File directory = new File("src/scripts/");
        File[] files = directory.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isFile() && !(file.getName().equals("start/FileWorker.json")))
                set.add(file.getName());
        }
    }
    @Override
    public void execute(String arguments) throws IOException {
        findScripts(list);
        System.out.print("В директории src/scripts скрипты: ");
        for (String nameOfScript : list)
            System.out.print(nameOfScript + " ");
        System.out.println();
        String fileName = ValidatorForCommand.checkExecuteScript(scanner, list);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("src/scripts/"+fileName))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.matches("execute_script file_name [A-Za-z0-9 ]+")) {
                    String[] parts = line.split(" ");
                    if (parts[2].equals(fileName))
                        continue;
                    innerScript(parts[2]);
                } else if (line.matches("add worker [0-9A-Za-z-,]+")) {
                    parseAdd(line);
                } else {
                    try {
                        switch (line) {
                            case "info" -> info.execute("");
                            case "show" -> show.execute("");
                            case "add worker" -> add.execute("");
                            case "update id worker" -> update_id.execute("");
                            case "remove_by_id id" -> removeByID.execute("");
                            case "clear" -> clear.execute("");
                            case "save" -> save.execute("");
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
                        System.out.println(Color.RED + "такой команды нет: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(Color.RED + "ошибка чтения файла");
        }
    }
    private void innerScript(String nameOfScript) throws IOException {
        File file = new File("src/scripts/"+nameOfScript);
        if (!file.exists()) {
            System.out.println(Color.RED + "вложенный скрипт не найден: " + nameOfScript);
            System.out.print(Color.RESET);
            return;
        }
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.matches("execute_script file_name [A-Za-z0-9]+")) {
                    String[] parts = line.split(" ");
                    if (parts[2].equals(nameOfScript))
                        continue;
                    innerScript(parts[2]);
                } else if (line.matches("add worker [0-9A-Za-z-,]+")) {
                    parseAdd(line);
                }
                else {
                    try {
                        switch (line) {
                            case "info" -> info.execute("");
                            case "show" -> show.execute("");
                            case "add worker" -> add.execute("");
                            case "update id worker" -> update_id.execute("");
                            case "remove_by_id id" -> removeByID.execute("");
                            case "clear" -> clear.execute("");
                            case "save" -> save.execute("");
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
                        System.out.println(Color.RED + "такой команды нет: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(Color.RED + "ошибка чтения вложенного скрипта: " + e.getMessage());
        }
    }
    private static void parseAdd(String line) {
        String[] commandAdd = line.split(" ");
        String[] workerLine = commandAdd[2].split(",");
        if (workerLine.length != 11) {
            System.out.println(Color.RED + "чтобы добавить работника, необходимо ввести 11 полей");
            return;
        }
        var workerName = ValidatorForCommand.checkNameAdd(workerLine[0]);
        if (workerName == null)
            return;
        var firstCoordinate = ValidatorForCommand.checkFirstCoordinateAdd(workerLine[1]);
        if (firstCoordinate == null)
            return;
        var secondCoordinate = ValidatorForCommand.checkSecondCoordinateAdd(workerLine[2]);
        if (secondCoordinate == null)
            return;
        var coordinates = new Coordinates(firstCoordinate, secondCoordinate);
        var workerSalary = ValidatorForCommand.checkSalaryAdd(workerLine[3]);
        if (workerSalary == null)
            return;
        var position = ValidatorForCommand.checkPositionAdd(workerLine[4]);
        var status = ValidatorForCommand.checkStatusAdd(workerLine[5]);
        if (status == null)
            return;
        var workerWeight = ValidatorForCommand.checkWeightAdd(workerLine[6]);
        if (workerWeight == null)
            return;
        var firstCoordinateOfLocation = ValidatorForCommand.checkFirstCoordinateOfLocationAdd(workerLine[7]);
        if (firstCoordinateOfLocation == null)
            return;
        var secondCoordinateOfLocation = ValidatorForCommand.checkSecondCoordinateOfLocationAdd(workerLine[8]);
        if (secondCoordinateOfLocation == null)
            return;
        var thirdCoordinateOfLocation = ValidatorForCommand.checkThirdCoordinateOfLocationAdd(workerLine[9]);
        if (thirdCoordinateOfLocation == null)
            return;
        var locationName = ValidatorForCommand.checkNameOfLocationAdd(workerLine[10]);
        if (locationName == null)
            return;
        var location = new Location(firstCoordinateOfLocation, secondCoordinateOfLocation, thirdCoordinateOfLocation, locationName);
        var person = new Person(workerWeight, location);
        var localDateTime = LocalDateTime.now();
        var localDate = LocalDate.now();
        Worker.resetGenerator(set);
        Worker worker = new Worker(workerName, coordinates, localDateTime, workerSalary, localDate, position, status, person);
        set.add(worker);
        System.out.println("Новый работник добавлен в коллекцию");
    }
}