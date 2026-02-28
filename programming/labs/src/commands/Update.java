package commands;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Map;
import SEclasses.*;
import check.ValidatorForCommand;
import check.ValidatorSE;
import design.Color;

public final class Update extends AbstractCommand{

    @Override
    public void execute(String arguments){
        try {
            if (!set.isEmpty()){
                Scanner scanner = new Scanner(System.in);
                Map<Integer, Worker> map = new HashMap<>();
                for (Worker worker : set){map.put(worker.getId(), worker);}
                System.out.println("Текущие id работников: "+map.keySet());
                int id = ValidatorForCommand.checkUpdate(scanner, map);
                Worker worker = map.get(id);
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
                    System.out.print(Position.values()[i]+" ");
                }
                System.out.println();
                Position position = ValidatorSE.checkPosition(scanner);
                worker.setPosition(position);
                System.out.print("Доступные статусы: ");
                for (byte i = 0; i < Status.values().length; i++) {
                    System.out.print(Status.values()[i]+" ");
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
            else
                throw new NoSuchElementException();
        } catch (NoSuchElementException e) {
            System.out.println(Color.RED + "не получится обновить, так как коллекция пуста. нужно сначала добавить хотя бы одно работника.");
        }
    }
}
