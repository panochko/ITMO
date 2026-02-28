package commands;

import java.util.Scanner;
import SEclasses.*;
import check.ValidatorSE;
import design.Color;

public final class RemoveGreater extends AbstractCommand{

    @Override
    public void execute(String arguments){
        if (set.isEmpty()) {
            System.out.println(Color.RED + "в коллекции нет элементов");
            return;
        }
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите максимальную заработную плату работника: ");
        int salary = ValidatorSE.checkSalary(scanner);
        byte counter = 0;
        for (Worker worker : set){
            if (worker.getSalary() > salary){
                set.remove(worker);
                counter++;
            }
        }
        System.out.println("Удалено "+counter+" элементов");
        System.out.println("Текущая коллекция:"+set);
    }
}
