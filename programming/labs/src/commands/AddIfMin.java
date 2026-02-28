package commands;

import SEclasses.*;
import check.ValidatorSE;
import design.Color;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public final class AddIfMin extends AbstractCommand{

    @Override
    public void execute(String arguments) {
        try {
            if (!set.isEmpty()) {
                Scanner scanner = new Scanner(System.in);
                String name = ValidatorSE.checkName(scanner);
                Integer x = ValidatorSE.checkFirstCoordinate(scanner);
                float y = ValidatorSE.checkSecondCoordinate(scanner);
                Coordinates coordinates = new Coordinates(x, y);
                LocalDateTime localDateTime = LocalDateTime.now();
                int salary = ValidatorSE.checkSalary(scanner);
                Map<Integer, Worker> map = new HashMap<>();
                for (Worker workerOfSet : set) {map.put(workerOfSet.getSalary(), workerOfSet);}
                int lowerestSalary = Collections.min(map.keySet());
                if (salary >= lowerestSalary){
                    System.out.println(Color.RED + "элемент не будет добавлен, так как зарплата нового работника не меньше минимальной зарплаты работника в коллекции");
                    return;
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
                Worker worker = new Worker(name, coordinates, localDateTime, salary, localDate, position, status, person);
                set.add(worker);
            }
            else
                throw new NoSuchElementException();
        } catch (NoSuchElementException e) {
            System.out.println(Color.RED + "сравнить работников не получится. коллекция пуста. нужно сначала добавить хотя бы одно работника.");
        }
    }
}
