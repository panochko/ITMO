package commands;
import SEclasses.*;

public final class Show extends AbstractCommand{

    @Override
    public void execute(String arguments) {
        if (arguments.equals("ArrayList") || arguments.equals("TreeSet") || arguments.equals("PriorityQueue")) {
            System.out.println("Текущие элементы в коллекции: ");
            for (Worker worker : collection)
                System.out.println(worker);
        }
        else {
            System.out.println("Текущие элементы в коллекции: ");
            for (Worker worker : mapCollection.values())
                System.out.println(worker);
        }
    }
}
