package commands;
import SEclasses.*;

public final class Clear extends AbstractCommand{
    @Override
    public void execute(String arguments){
        if (arguments.equals("ArrayList") || arguments.equals("TreeSet") || arguments.equals("PriorityQueue")) {
            collection.clear();
            System.out.println("Коллекция пуста");
        }
        else {
            mapCollection.clear();
            System.out.println("Коллекция пуста");
        }
    }
}
