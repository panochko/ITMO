package commands;
import SEclasses.*;

import java.io.Serial;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class Info extends AbstractCommand{
    @Serial
    private static final long serialVersionUID = -4798440035591391262L;

    public Info(String commandName) {
        super(commandName);
    }
    @Override
    public List<String> executeWithIntAndCollection(Integer number, Collection<Worker> collection, Map<Integer, Worker> mapCollection){
        stringList.clear();
        LocalDate localDate = LocalDate.now();
        if (mapCollection == null)
            stringList.add("Тип коллекции: "+collection.getClass()+"\nДата инициализации: "+localDate+"\nКоличество элементов коллекции: "+number);
        if (collection == null)
            stringList.add("Тип коллекции: " + mapCollection.getClass() + "\nДата инициализации: " + localDate + "\nКоличество элементов коллекции: " + number);
        return stringList;
    }
}
