package commands;
import SEclasses.*;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class Show extends AbstractCommand{
    @Serial
    private static final long serialVersionUID = -3676334378970386186L;

    public Show(String commandName) {
        super(commandName);
    }
    @Override
    public List<Worker> executeWithCollection(Collection<Worker> collection, Map<Integer, Worker> mapCollection) {
        list.clear();
        if (mapCollection == null)
            list.addAll(collection);
        if (collection == null)
            list.addAll(mapCollection.values());
        return list;
    }
}
