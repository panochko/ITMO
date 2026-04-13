package commands;

import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import SEclasses.*;
import check.ValidatorSE;

public final class Update extends AbstractCommand{
    @Serial
    private static final long serialVersionUID = -8148480872745200388L;

    public Update(String commandName) {
        super(commandName);
    }
    @Override
    public Worker executeWithIntAndReturn(Integer number, Collection<Worker> collection, Map<Integer, Worker> mapCollection) {
        try {
            if (mapCollection == null) {
                Scanner scanner = new Scanner(System.in);
                Map<Integer, Worker> map = new HashMap<>();
                for (Worker worker : collection)
                    map.put(worker.getId(), worker);
                Worker worker = map.get(number);
                worker.setName(ValidatorSE.checkName(scanner));
                Integer x = ValidatorSE.checkFirstCoordinate(scanner);
                float y = ValidatorSE.checkSecondCoordinate(scanner);
                Coordinates coordinates = new Coordinates(x, y);
                worker.setCoordinates(coordinates);
                LocalDateTime localDateTime = LocalDateTime.now();
                worker.setCreationDate(localDateTime);
                int salary = ValidatorSE.checkSalary(scanner);
                worker.setSalary(salary);
                LocalDate localDate = LocalDate.now();
                worker.setStartDate(localDate);
                System.out.print("Доступные должности: ");
                for (byte i = 0; i < Position.values().length; i++) {
                    System.out.print(Position.values()[i] + " ");
                }
                System.out.println();
                Position position = ValidatorSE.checkPosition(scanner);
                worker.setPosition(position);
                System.out.print("Доступные статусы: ");
                for (byte i = 0; i < Status.values().length; i++) {
                    System.out.print(Status.values()[i] + " ");
                }
                System.out.println();
                Status status = ValidatorSE.checkStatus(scanner);
                worker.setStatus(status);
                double weight = ValidatorSE.checkWeight(scanner);
                System.out.println("Адрес работника...");
                Integer xLocation = ValidatorSE.checkFirstCoordinateOfLocation(scanner);
                long yLocation = ValidatorSE.checkSecondCoordinateOfLocation(scanner);
                long zLocation = ValidatorSE.checkThirdCoordinateOfLocation(scanner);
                String nameLocation = ValidatorSE.checkNameOfLocation(scanner);
                Location location = new Location(xLocation, yLocation, zLocation, nameLocation);
                Person person = new Person(weight, location);
                worker.setPerson(person);
            }
        } catch (NoSuchElementException e) {
            return null;
        }
        try {
            if (collection == null) {
                Scanner scanner = new Scanner(System.in);
                Map<Integer, Worker> map = new HashMap<>();
                for (Worker worker : mapCollection.values())
                    map.put(worker.getId(), worker);
                Worker worker = map.get(number);
                worker.setName(ValidatorSE.checkName(scanner));
                Integer x = ValidatorSE.checkFirstCoordinate(scanner);
                float y = ValidatorSE.checkSecondCoordinate(scanner);
                Coordinates coordinates = new Coordinates(x, y);
                worker.setCoordinates(coordinates);
                LocalDateTime localDateTime = LocalDateTime.now();
                worker.setCreationDate(localDateTime);
                int salary = ValidatorSE.checkSalary(scanner);
                worker.setSalary(salary);
                LocalDate localDate = LocalDate.now();
                worker.setStartDate(localDate);
                System.out.print("Доступные должности: ");
                for (byte i = 0; i < Position.values().length; i++) {
                    System.out.print(Position.values()[i] + " ");
                }
                System.out.println();
                Position position = ValidatorSE.checkPosition(scanner);
                worker.setPosition(position);
                System.out.print("Доступные статусы: ");
                for (byte i = 0; i < Status.values().length; i++) {
                    System.out.print(Status.values()[i] + " ");
                }
                System.out.println();
                Status status = ValidatorSE.checkStatus(scanner);
                worker.setStatus(status);
                double weight = ValidatorSE.checkWeight(scanner);
                System.out.println("Адрес работника...");
                Integer xLocation = ValidatorSE.checkFirstCoordinateOfLocation(scanner);
                long yLocation = ValidatorSE.checkSecondCoordinateOfLocation(scanner);
                long zLocation = ValidatorSE.checkThirdCoordinateOfLocation(scanner);
                String nameLocation = ValidatorSE.checkNameOfLocation(scanner);
                Location location = new Location(xLocation, yLocation, zLocation, nameLocation);
                Person person = new Person(weight, location);
                worker.setPerson(person);
            }
        } catch (NoSuchElementException e) {
            return null;
        }
        return null;
    }
}
