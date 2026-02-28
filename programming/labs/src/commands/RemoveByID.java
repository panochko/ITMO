package commands;
import SEclasses.*;
import check.ValidatorSE;
import design.Color;

import java.util.*;

public final class RemoveByID extends AbstractCommand{

    @Override
    public void execute(String arguments){
        if (set.isEmpty()) {
            System.out.println(Color.RED + "в коллекции нет элементов");
            return;
        }
        Scanner scanner = new Scanner(System.in);
        List<Integer> list = new ArrayList<>();
        for (Worker worker : set){list.add(worker.getId());}
        System.out.println("Текущие id работников: "+list);
        int id = ValidatorSE.checkID(scanner, list);
        set.removeIf(worker -> id == worker.getId());
        System.out.println("Элемент с id "+id+" удален из коллекции");
    }
}
