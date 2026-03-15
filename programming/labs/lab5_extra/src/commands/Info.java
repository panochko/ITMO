package commands;
import SEclasses.*;
import java.time.LocalDate;

public final class Info extends AbstractCommand{

    @Override
    public void execute(String arguments){
        if (arguments.equals("ArrayList") || arguments.equals("TreeSet") || arguments.equals("PriorityQueue")) {
            LocalDate localDate = LocalDate.now();
            System.out.println("Тип коллекции: "+collection.getClass()+"\nДата инициализации: "+localDate+"\nКоличество элементов коллекции: "+collection.size());
        }
        else {
            LocalDate localDate = LocalDate.now();
            System.out.println("Тип коллекции: "+mapCollection.getClass()+"\nДата инициализации: "+localDate+"\nКоличество элементов коллекции: "+mapCollection.size());
        }
    }
}
