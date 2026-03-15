package commands;
import SEclasses.*;
import java.io.IOException;
import java.util.*;

public abstract class AbstractCommand{
    public static Collection<Worker> collection;
    public static Map<Integer, Worker> mapCollection;
    //Коллекции типа TreeMap и TreeSet требует, чтобы объект коллекции реализовывал интерфейс Comparable
    public abstract void execute(String arguments) throws IOException;
}