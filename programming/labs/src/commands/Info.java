package commands;
import SEclasses.*;
import java.time.LocalDate;

public final class Info extends AbstractCommand{

    @Override
    public void execute(String arguments){
        LocalDate localDate = LocalDate.now();
        System.out.println("Тип коллекции: "+set.getClass()+"\nДата инициализации: "+localDate+"\nКоличество элементов коллекции: "+set.size());
    }
}
