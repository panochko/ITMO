package commands;

import SEclasses.*;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.Map;


public final class FilterContainsName extends AbstractCommand {
    @Serial
    private static final long serialVersionUID = -5432278280189499988L;

    public FilterContainsName(String commandName) {
        super(commandName);
    }
    @Override
    public List<Worker> executeWithArgsAndCollection(String argument, Collection<Worker> collection, Map<Integer, Worker> mapCollection) {
        list.clear();
        if (mapCollection == null) {
             collection.stream()
                    .filter(worker -> worker.getName().contains(argument))
                    .forEach(list::add);
        }
        if (collection == null) {
            mapCollection.values().stream()
                    .filter(worker -> worker.getName().contains(argument))
                    .forEach(list::add);
        }
        return list;
    }
}
