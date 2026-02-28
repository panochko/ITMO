package commands;
import SEclasses.*;

public final class Clear extends AbstractCommand{

    @Override
    public void execute(String arguments){
        set.clear();
        System.out.println("Коллекция пуста");
    }
}
