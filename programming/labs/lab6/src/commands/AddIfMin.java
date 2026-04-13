package commands;

import SEclasses.*;
import check.ValidatorSE;
import design.Color;

import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public final class AddIfMin extends AbstractCommand {
    @Serial
    private static final long serialVersionUID = -2262092826177861652L;

    public AddIfMin(String commandName) {
        super(commandName);
    }
    @Override
    public Worker executeWithReturn() {
        try {
            Scanner scanner = new Scanner(System.in);
            String name = ValidatorSE.checkName(scanner);
            Integer x = ValidatorSE.checkFirstCoordinate(scanner);
            float y = ValidatorSE.checkSecondCoordinate(scanner);
            Coordinates coordinates = new Coordinates(x, y);
            LocalDateTime localDateTime = LocalDateTime.now();
            int salary = ValidatorSE.checkSalary(scanner);
            Map<Integer, Worker> map = new HashMap<>();
            for (Worker workerOfSet : collection) {
                map.put(workerOfSet.getSalary(), workerOfSet);
            }
            int lowerestSalary = Collections.min(map.keySet());
            if (salary >= lowerestSalary) {
                System.out.println(Color.RED + "элемент не будет добавлен, так как зарплата нового работника не меньше минимальной зарплаты работника в коллекции");
                return null;
            }
            LocalDate localDate = LocalDate.now();
            System.out.print("Доступные должности: ");
            for (byte i = 0; i < Position.values().length; i++) {
                System.out.print(Position.values()[i] + " ");
            }
            System.out.println();
            Position position = ValidatorSE.checkPosition(scanner);
            System.out.print("Доступные статусы: ");
            for (byte i = 0; i < Status.values().length; i++) {
                System.out.print(Status.values()[i] + " ");
            }
            System.out.println();
            Status status = ValidatorSE.checkStatus(scanner);
            double weight = ValidatorSE.checkWeight(scanner);
            System.out.println("Адрес работника...");
            Integer xLocation = ValidatorSE.checkFirstCoordinateOfLocation(scanner);
            long yLocation = ValidatorSE.checkSecondCoordinateOfLocation(scanner);
            long zLocation = ValidatorSE.checkThirdCoordinateOfLocation(scanner);
            String nameLocation = ValidatorSE.checkNameOfLocation(scanner);
            Location location = new Location(xLocation, yLocation, zLocation, nameLocation);
            Person person = new Person(weight, location);
            return new Worker(name, coordinates, localDateTime, salary, localDate, position, status, person);
        } catch (NullPointerException e) {
            return null;
        }
    }
}