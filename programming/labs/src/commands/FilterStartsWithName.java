package commands;

import java.util.Scanner;
import SEclasses.*;
import design.Color;

public final class FilterStartsWithName extends AbstractCommand{

    @Override
    public void execute(String arguments){
        if (set.isEmpty()){
            System.out.println(Color.RED + "в коллекции нет элементов");
            return;
        }
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите подстроку, с которой должно начинаться имя: ");
        String startsPattern = scanner.nextLine();
        System.out.print("Работники, у которых имя начинается с "+"\""+startsPattern+"\""+": ");
        for (Worker worker : set){
            if (worker.getName().startsWith(startsPattern))
                System.out.println(worker);
        }
    }
}
