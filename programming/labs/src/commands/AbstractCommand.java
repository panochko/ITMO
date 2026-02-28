package commands;
import SEclasses.*;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

public abstract class AbstractCommand{

    protected static Set<Worker> set = new TreeSet<>();
    //Коллекции типа TreeMap и TreeSet требует, чтобы объект коллекции реализовывал интерфейс Comparable
    public abstract void execute(String arguments) throws IOException;
}