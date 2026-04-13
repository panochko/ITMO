package commands;
import SEclasses.*;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class Clear extends AbstractCommand{
    @Serial
    private static final long serialVersionUID = 1659497000412275669L;

    public Clear(String commandName) {
        super(commandName);
    }
    @Override
    public List<String> executeWithCollection(Collection<Worker> collection, Map<Integer, Worker> mapCollection) {
        stringList.clear();
        if (collection == null) {
            mapCollection.clear();
            stringList.add("коллекция интерфейса Map пуста");
        }
        if (mapCollection == null) {
            collection.clear();
            stringList.add("коллекция интерфейса Collection пуста");
        }
        return stringList;
    }
}
