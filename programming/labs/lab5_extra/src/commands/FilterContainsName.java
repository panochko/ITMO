package commands;

import java.util.Scanner;
import SEclasses.*;
import design.Color;

public final class FilterContainsName extends AbstractCommand{

    @Override
    public void execute(String arguments){
        if (arguments.equals("ArrayList") || arguments.equals("TreeSet") || arguments.equals("PriorityQueue")) {
            if (collection.isEmpty()){
                System.out.println(Color.RED + "в коллекции нет элементов");
                return;
            }
            Scanner scanner = new Scanner(System.in);
            System.out.print("Введите желаемую подстроку в имени: ");
            String pattern = scanner.nextLine();
            System.out.println(Color.RESET + "Работники, у которых в имени есть "+"\""+pattern+"\""+": ");
            for (Worker worker : collection){
                if (worker.getName().contains(pattern))
                    System.out.println(worker);
            }
        }
        else {
            if (mapCollection.isEmpty()){
                System.out.println(Color.RED + "в коллекции нет элементов");
                return;
            }
            Scanner scanner = new Scanner(System.in);
            System.out.print("Введите желаемую подстроку в имени: ");
            String pattern = scanner.nextLine();
            System.out.println(Color.RESET + "Работники, у которых в имени есть "+"\""+pattern+"\""+": ");
            for (Worker worker : mapCollection.values()){
                if (worker.getName().contains(pattern))
                    System.out.println(worker);
            }
        }
    }
}
