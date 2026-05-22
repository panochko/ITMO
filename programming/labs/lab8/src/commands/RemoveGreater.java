package commands;

import java.io.Serial;
import java.util.*;

import SEclasses.*;

public final class RemoveGreater extends AbstractCommand{
    @Serial
    private static final long serialVersionUID = 3015744332881668686L;

    public RemoveGreater(String commandName) {
        super(commandName);
    }
    @Override
    public List<Worker> executeWithIntAndCollection(Integer number, Collection<Worker> collection, Map<Integer, Worker> mapCollection){
        list.clear();
        if (mapCollection ==  null){
            collection.removeIf(worker -> worker.getSalary() > number);
            list.addAll(collection);
        }
        if (collection == null) {
            mapCollection.values().removeIf(worker -> worker.getSalary() > number);
            list.addAll(mapCollection.values());
        }
        return list;
    }
}
