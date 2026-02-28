package commands;

import SEclasses.*;
import check.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Scanner;

public final class Add extends AbstractCommand{
    // мб тут реализовать worker
    @Override
    public void execute(String arguments){
        Scanner scanner = new Scanner(System.in);
        //int id = ValidatorSE.checkID(scanner);
        String name = ValidatorSE.checkName(scanner);
        Integer x = ValidatorSE.checkFirstCoordinate(scanner);
        float y = ValidatorSE.checkSecondCoordinate(scanner);
        Coordinates coordinates = new Coordinates(x, y);
        LocalDateTime localDateTime = LocalDateTime.now();
        int salary = ValidatorSE.checkSalary(scanner);
        LocalDate localDate = LocalDate.now();
        System.out.print("Доступные должности: ");
        for (byte i = 0; i < Position.values().length; i++) {
            System.out.print(Position.values()[i]+" ");
        }
        System.out.println();
        Position position = ValidatorSE.checkPosition(scanner);
        System.out.print("Доступные статусы: ");
        for (byte i = 0; i < Status.values().length; i++) {
            System.out.print(Status.values()[i]+" ");
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
        Worker worker = new Worker(name, coordinates, localDateTime, salary, localDate, position, status, person);
        // print json-information of worker at console
        set.add(worker);
    }
}
