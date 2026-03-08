package commands;

import java.util.Scanner;
import java.util.Set;

import SEclasses.*;
import check.ValidatorSE;
import design.Color;

public final class RemoveGreater extends AbstractCommand{
    @Override
    public void execute(String arguments){
        if (set.isEmpty()) {
            System.out.println(Color.RED + "в коллекции нет элементов");
            System.out.print(Color.RESET);
            return;
        }
        Scanner scanner = new Scanner(System.in);
        int salary = ValidatorSE.checkSalary(scanner);
        set.removeIf(worker -> worker.getSalary() > salary);
        System.out.println("Текущая коллекция:");
        for (Worker worker : set)
            System.out.println(worker);
    }
}
