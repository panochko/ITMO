package commands;
import SEclasses.*;

public final class Show extends AbstractCommand{

    @Override
    public void execute(String arguments){
        System.out.println("Текущие элементы в коллекции: ");
        for (Worker worker : set){
            System.out.println(worker);
        }
    }
}
