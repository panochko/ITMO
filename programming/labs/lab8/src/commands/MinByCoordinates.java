package commands;

import java.io.Serial;
import java.util.*;

import SEclasses.*;

public final class MinByCoordinates extends  AbstractCommand{
    @Serial
    private static final long serialVersionUID = -2335149576508794902L;

    public MinByCoordinates(String commandName) {
        super(commandName);
    }
    @Override
    public List<Worker> executeWithCollection(Collection<Worker> collection, Map<Integer, Worker> mapCollection){
        list.clear();
        if (mapCollection == null){
            Map<Integer, Worker> map = new HashMap<>();
            for (Worker worker : collection){map.put(worker.getCoordinates().hashCode(), worker);}
            int minCoordinates = Collections.min(map.keySet());
            Worker worker = map.get(minCoordinates);
            list.add(worker);
        }
        if (collection == null) {
            Map<Integer, Worker> map = new HashMap<>();
            for (Worker worker : mapCollection.values()){map.put(worker.getCoordinates().hashCode(), worker);}
            int minCoordinates = Collections.min(map.keySet());
            Worker worker = map.get(minCoordinates);
            list.add(worker);
        }
        return list;
    }
}
