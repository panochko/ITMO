package commands;
import SEclasses.*;
import check.ValidatorSE;
import design.Color;

import java.util.*;

public final class RemoveByID extends AbstractCommand{
    @Override
    public void execute(String arguments){
        if (arguments.equals("ArrayList") || arguments.equals("TreeSet") || arguments.equals("PriorityQueue")) {
            if (collection.isEmpty()) {
                System.out.println(Color.RED + "в коллекции нет элементов");
                System.out.print(Color.RESET);
                return;
            }
            Scanner scanner = new Scanner(System.in);
            List<Integer> list = new ArrayList<>();
            for (Worker worker : collection){list.add(worker.getId());}
            System.out.println("Текущие id работников: "+list);
            int id = ValidatorSE.checkID(scanner, list);
            collection.removeIf(worker -> id == worker.getId());
            System.out.println("Элемент с id "+id+" удален из коллекции");
        }
        else {
            if (mapCollection.isEmpty()) {
                System.out.println(Color.RED + "в коллекции нет элементов");
                System.out.print(Color.RESET);
                return;
            }
            Scanner scanner = new Scanner(System.in);
            List<Integer> list = new ArrayList<>();
            for (Worker worker : mapCollection.values()){list.add(worker.getId());}
            System.out.println("Текущие id работников: "+list);
            int id = ValidatorSE.checkID(scanner, list);
            mapCollection.values().removeIf(worker -> id == worker.getId());
            System.out.println("Элемент с id "+id+" удален из коллекции");
        }
    }
}
