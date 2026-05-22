package commands;

import SEclasses.*;
import check.ValidatorForCommand;
import design.Color;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
public final class ExecuteScript extends AbstractCommand {
    @Serial
    private static final long serialVersionUID = 6887808669503015556L;
    public ExecuteScript(String commandName) {
        super(commandName);
    }
    private final Set<String> set = new TreeSet<>();
    transient Scanner scanner = new Scanner(System.in);
    private void findScripts(Set<String> set){
        File directory = new File("src/scripts/");
        File[] files = directory.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isFile() && !(file.getName().equals("server/FileWorker.json")))
                set.add(file.getName());
        }
    }
    @Override
    public void executeScript() {
        stringList.clear();
        findScripts(set);
        System.out.print(Color.RESET + "В директории src/scripts скрипты: ");
        for (String nameOfScript : set)
            System.out.print(nameOfScript + " ");
        System.out.println();
        String fileName = ValidatorForCommand.checkExecuteScript(scanner, set);
        try (var bufferedReader = new BufferedReader(new FileReader("src/scripts/"+fileName))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.matches("execute_script [A-Za-zА-Яа-я0-9_. ]+")) {
                    String[] parts = line.split(" ", 2);
                    if (parts[1].equals(fileName))
                        continue;
                    innerScript(parts[1], fileName);
                } else
                    stringList.add(line);
            }
        } catch (IOException e) {
            System.out.println(Color.RED + "ошибка чтения файла");
        }
    }
    public void innerScript(String nameOfScript, String fileName) throws IOException {
        File file = new File("src/scripts/"+nameOfScript);
        if (!file.exists()) {
            stringList.add("вложенный скрипт не найден: " + nameOfScript);
            return;
        }
        try (var bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.matches("execute_script [A-Za-zА-Яа-я0-9_. ]+")) {
                    String[] parts = line.split(" ", 2);
                    if (parts[1].equals(nameOfScript) || parts[1].equals(fileName))
                        continue;
                    innerScript(parts[1], fileName);
                }
                else
                    stringList.add(line);
            }
        } catch (IOException e) {
            System.out.println(Color.RED + "ошибка чтения вложенного скрипта: " + e.getMessage());
        }
    }
    public Worker parseAdd(String line, Collection<Worker> collection, Map<Integer, Worker> mapCollection) {
        String[] commandAdd = line.split(" ");
        String[] workerLine = commandAdd[1].split(",");
        if (workerLine.length != 11) return null;
        var workerName = ValidatorForCommand.checkNameAdd(workerLine[0]);
        if (workerName == null) return null;
        var firstCoordinate = ValidatorForCommand.checkFirstCoordinateAdd(workerLine[1]);
        if (firstCoordinate == null) return null;
        var secondCoordinate = ValidatorForCommand.checkSecondCoordinateAdd(workerLine[2]);
        if (secondCoordinate == null) return null;
        var coordinates = new Coordinates(firstCoordinate, secondCoordinate);
        var workerSalary = ValidatorForCommand.checkSalaryAdd(workerLine[3]);
        if (workerSalary == null) return null;
        var position = ValidatorForCommand.checkPositionAdd(workerLine[4]);
        if (position == null) return null;
        var status = ValidatorForCommand.checkStatusAdd(workerLine[5]);
        if (status == null) return null;
        var workerWeight = ValidatorForCommand.checkWeightAdd(workerLine[6]);
        if (workerWeight == null) return null;
        var firstCoordinateOfLocation = ValidatorForCommand.checkFirstCoordinateOfLocationAdd(workerLine[7]);
        if (firstCoordinateOfLocation == null) return null;
        var secondCoordinateOfLocation = ValidatorForCommand.checkSecondCoordinateOfLocationAdd(workerLine[8]);
        if (secondCoordinateOfLocation == null) return null;
        var thirdCoordinateOfLocation = ValidatorForCommand.checkThirdCoordinateOfLocationAdd(workerLine[9]);
        if (thirdCoordinateOfLocation == null) return null;
        var locationName = ValidatorForCommand.checkNameOfLocationAdd(workerLine[10]);
        if (locationName == null) return null;
        var location = new Location(firstCoordinateOfLocation, secondCoordinateOfLocation, thirdCoordinateOfLocation, locationName);
        var person = new Person(workerWeight, location);
        var localDateTime = LocalDateTime.now();
        var localDate = LocalDate.now();
        return new Worker(workerName, coordinates, localDateTime, workerSalary, localDate, position, status, person);
    }
    public Worker parseAddIfMax(String line, Collection<Worker> collection, Map<Integer, Worker> mapCollection) {
        String[] commandAdd = line.split(" ");
        String[] workerLine = commandAdd[1].split(",");
        if (workerLine.length != 11) return null;
        var workerName = ValidatorForCommand.checkNameAdd(workerLine[0]);
        if (workerName == null) return null;
        var firstCoordinate = ValidatorForCommand.checkFirstCoordinateAdd(workerLine[1]);
        if (firstCoordinate == null) return null;
        var secondCoordinate = ValidatorForCommand.checkSecondCoordinateAdd(workerLine[2]);
        if (secondCoordinate == null) return null;
        var coordinates = new Coordinates(firstCoordinate, secondCoordinate);
        var workerSalary = ValidatorForCommand.checkSalaryAdd(workerLine[3]);
        if (workerSalary == null) return null;
        if (mapCollection == null && collection != null) {
            Map<Integer, Worker> map = new HashMap<>();
            for (Worker workerOfSet : collection)
                map.put(workerOfSet.getSalary(), workerOfSet);
            int biggestSalary = Collections.max(map.keySet());
            if (workerSalary <= biggestSalary) return null;
        }
        if (collection == null && mapCollection != null) {
            Map<Integer, Worker> map = new HashMap<>();
            for (Worker workerOfSet : mapCollection.values())
                map.put(workerOfSet.getSalary(), workerOfSet);
            int biggestSalary = Collections.max(map.keySet());
            if (workerSalary <= biggestSalary) return null;
        }
        var position = ValidatorForCommand.checkPositionAdd(workerLine[4]);
        if (position == null) return null;
        var status = ValidatorForCommand.checkStatusAdd(workerLine[5]);
        if (status == null) return null;
        var workerWeight = ValidatorForCommand.checkWeightAdd(workerLine[6]);
        if (workerWeight == null) return null;
        var firstCoordinateOfLocation = ValidatorForCommand.checkFirstCoordinateOfLocationAdd(workerLine[7]);
        if (firstCoordinateOfLocation == null) return null;
        var secondCoordinateOfLocation = ValidatorForCommand.checkSecondCoordinateOfLocationAdd(workerLine[8]);
        if (secondCoordinateOfLocation == null) return null;
        var thirdCoordinateOfLocation = ValidatorForCommand.checkThirdCoordinateOfLocationAdd(workerLine[9]);
        if (thirdCoordinateOfLocation == null) return null;
        var locationName = ValidatorForCommand.checkNameOfLocationAdd(workerLine[10]);
        if (locationName == null) return null;
        var location = new Location(firstCoordinateOfLocation, secondCoordinateOfLocation, thirdCoordinateOfLocation, locationName);
        var person = new Person(workerWeight, location);
        var localDateTime = LocalDateTime.now();
        var localDate = LocalDate.now();
        return new Worker(workerName, coordinates, localDateTime, workerSalary, localDate, position, status, person);
    }
    public Worker parseAddIfMin(String line, Collection<Worker> collection, Map<Integer, Worker> mapCollection) {
        String[] commandAdd = line.split(" ");
        String[] workerLine = commandAdd[1].split(",");
        if (workerLine.length != 11) return null;
        var workerName = ValidatorForCommand.checkNameAdd(workerLine[0]);
        if (workerName == null) return null;
        var firstCoordinate = ValidatorForCommand.checkFirstCoordinateAdd(workerLine[1]);
        if (firstCoordinate == null) return null;
        var secondCoordinate = ValidatorForCommand.checkSecondCoordinateAdd(workerLine[2]);
        if (secondCoordinate == null) return null;
        var coordinates = new Coordinates(firstCoordinate, secondCoordinate);
        var workerSalary = ValidatorForCommand.checkSalaryAdd(workerLine[3]);
        if (workerSalary == null) return null;
        if (mapCollection == null && collection != null) {
            Map<Integer, Worker> map = new HashMap<>();
            for (Worker workerOfSet : collection)
                map.put(workerOfSet.getSalary(), workerOfSet);
            int lowestSalary = Collections.min(map.keySet());
            if (workerSalary >= lowestSalary) return null;
        }
        if (collection == null && mapCollection != null) {
            Map<Integer, Worker> map = new HashMap<>();
            for (Worker workerOfSet : mapCollection.values())
                map.put(workerOfSet.getSalary(), workerOfSet);
            int lowestSalary = Collections.min(map.keySet());
            if (workerSalary >= lowestSalary) return null;
        }
        var position = ValidatorForCommand.checkPositionAdd(workerLine[4]);
        if (position == null) return null;
        var status = ValidatorForCommand.checkStatusAdd(workerLine[5]);
        if (status == null) return null;
        var workerWeight = ValidatorForCommand.checkWeightAdd(workerLine[6]);
        if (workerWeight == null) return null;
        var firstCoordinateOfLocation = ValidatorForCommand.checkFirstCoordinateOfLocationAdd(workerLine[7]);
        if (firstCoordinateOfLocation == null) return null;
        var secondCoordinateOfLocation = ValidatorForCommand.checkSecondCoordinateOfLocationAdd(workerLine[8]);
        if (secondCoordinateOfLocation == null) return null;
        var thirdCoordinateOfLocation = ValidatorForCommand.checkThirdCoordinateOfLocationAdd(workerLine[9]);
        if (thirdCoordinateOfLocation == null) return null;
        var locationName = ValidatorForCommand.checkNameOfLocationAdd(workerLine[10]);
        if (locationName == null) return null;
        var location = new Location(firstCoordinateOfLocation, secondCoordinateOfLocation, thirdCoordinateOfLocation, locationName);
        var person = new Person(workerWeight, location);
        var localDateTime = LocalDateTime.now();
        var localDate = LocalDate.now();
        return new Worker(workerName, coordinates, localDateTime, workerSalary, localDate, position, status, person);
    }
}