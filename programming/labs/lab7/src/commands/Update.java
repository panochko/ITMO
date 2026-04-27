package commands;

import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import SEclasses.*;
import check.ValidatorSE;

public final class Update extends AbstractCommand {
    @Serial
    private static final long serialVersionUID = -8148480872745200388L;

    public Update(String commandName) {
        super(commandName);
    }

    @Override
    public Worker executeWithIntAndReturn(Integer number, Collection<Worker> collection, Map<Integer, Worker> mapCollection) {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("введите имя работника: ");
            String name = ValidatorSE.checkName(scanner);
            Integer x = ValidatorSE.checkFirstCoordinate(scanner);
            float y = ValidatorSE.checkSecondCoordinate(scanner);
            Coordinates coordinates = new Coordinates(x, y);
            int salary = ValidatorSE.checkSalary(scanner);
            System.out.print("доступные должности: ");
            for (Position p : Position.values()) System.out.print(p + " ");
            System.out.println();
            Position position = ValidatorSE.checkPosition(scanner);
            System.out.print("доступные статусы: ");
            for (Status s : Status.values()) System.out.print(s + " ");
            System.out.println();
            Status status = ValidatorSE.checkStatus(scanner);
            double weight = ValidatorSE.checkWeight(scanner);
            System.out.println("адрес работника...");
            Integer xLoc = ValidatorSE.checkFirstCoordinateOfLocation(scanner);
            long yLoc = ValidatorSE.checkSecondCoordinateOfLocation(scanner);
            long zLoc = ValidatorSE.checkThirdCoordinateOfLocation(scanner);
            String nameLoc = ValidatorSE.checkNameOfLocation(scanner);
            Location location = new Location(xLoc, yLoc, zLoc, nameLoc);
            Person person = new Person(weight, location);
            return new Worker(name, coordinates, LocalDateTime.now(), salary, LocalDate.now(), position, status, person);
        } catch (NoSuchElementException e) {
            return null;
        }
    }
}