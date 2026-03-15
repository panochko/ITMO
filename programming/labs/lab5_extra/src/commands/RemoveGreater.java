package commands;

import java.util.Scanner;
import java.util.Set;

import SEclasses.*;
import check.ValidatorSE;
import design.Color;

public final class RemoveGreater extends AbstractCommand{
    @Override
    public void execute(String arguments){
        if (arguments.equals("ArrayList") || arguments.equals("TreeSet") || arguments.equals("PriorityQueue")) {
            if (collection.isEmpty()) {
                System.out.println(Color.RED + "в коллекции нет элементов");
                System.out.print(Color.RESET);
                return;
            }
            Scanner scanner = new Scanner(System.in);
            int salary = ValidatorSE.checkSalary(scanner);
            collection.removeIf(worker -> worker.getSalary() > salary);
            System.out.println("Текущая коллекция:");
            for (Worker worker : collection)
                System.out.println(worker);
        }
        else {
            if (mapCollection.isEmpty()) {
                System.out.println(Color.RED + "в коллекции нет элементов");
                System.out.print(Color.RESET);
                return;
            }
            Scanner scanner = new Scanner(System.in);
            int salary = ValidatorSE.checkSalary(scanner);
            mapCollection.values().removeIf(worker -> worker.getSalary() > salary);
            System.out.println("Текущая коллекция:");
            for (Worker worker : mapCollection.values())
                System.out.println(worker);
        }
    }
}
