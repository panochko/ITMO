package commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import SEclasses.*;
import design.Color;

public final class MinByCoordinates extends  AbstractCommand{
    @Override
    public void execute(String arguments){
        if (arguments.equals("ArrayList") || arguments.equals("TreeSet") || arguments.equals("PriorityQueue")) {
            if (collection.isEmpty()){
                System.out.println(Color.RED + "в коллекции нет элементов");
                System.out.print(Color.RESET);
                return;
            }
            Map<Integer, Worker> map = new HashMap<>();
            for (Worker worker : collection){map.put(worker.getCoordinates().hashCode(), worker);}
            int minCoordinates = Collections.min(map.keySet());
            Worker worker = map.get(minCoordinates);
            System.out.println(worker);
        }
        else {
            if (mapCollection.isEmpty()){
                System.out.println(Color.RED + "в коллекции нет элементов");
                System.out.print(Color.RESET);
                return;
            }
            Map<Integer, Worker> map = new HashMap<>();
            for (Worker worker : mapCollection.values()){map.put(worker.getCoordinates().hashCode(), worker);}
            int minCoordinates = Collections.min(map.keySet());
            Worker worker = map.get(minCoordinates);
            System.out.println(worker);
        }
    }
}
