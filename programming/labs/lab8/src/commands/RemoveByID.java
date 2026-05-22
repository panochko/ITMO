package commands;
import SEclasses.*;
import java.io.Serial;
import java.util.*;

public final class RemoveByID extends AbstractCommand {
    @Serial
    private static final long serialVersionUID = 8567010502374299437L;

    public RemoveByID(String commandName) {
        super(commandName);
    }
    @Override
    public List<String> executeWithIntAndCollection(Integer number, Collection<Worker> collection, Map<Integer, Worker> mapCollection){
        stringList.clear();
        if (mapCollection == null) {
            collection.removeIf(worker -> number == worker.getId());
            stringList.add("Элемент с id " + number + " удален из коллекции");
        }
        if (collection == null) {
            mapCollection.values().removeIf(worker -> number == worker.getId());
            stringList.add("Элемент с id " + number + " удален из коллекции");
        }
        return stringList;
    }
}