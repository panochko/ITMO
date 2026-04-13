package commands;
import SEclasses.*;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class FilterStartsWithName extends AbstractCommand {
    @Serial
    private static final long serialVersionUID = 1823992312145678252L;

    public FilterStartsWithName(String commandName) {
        super(commandName);
    }
    @Override
    public List<Worker> executeWithArgsAndCollection(String argument, Collection<Worker> collection, Map<Integer, Worker> mapCollection) {
        list.clear();
        if (mapCollection == null) {
            collection.stream()
                    .filter(worker -> worker.getName().startsWith(argument))
                    .forEach(list::add);
        }
        if (collection == null) {
            mapCollection.values().stream()
                    .filter(worker -> worker.getName().startsWith(argument))
                    .forEach(list::add);
        }
        return list;
    }
}

