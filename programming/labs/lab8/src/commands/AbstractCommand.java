package commands;
import SEclasses.*;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public abstract class AbstractCommand implements Serializable {
    @Serial
    private static final long serialVersionUID = 2414214836222522630L;
    protected List<Worker> list = new ArrayList<>();
    protected static List<String> stringList = new ArrayList<>();
    private static Collection<Worker> collection;
    private static Map<Integer, Worker> mapCollection;
    private String commandName;
    public AbstractCommand(String commandName) {
        this.commandName = commandName;
    }
    public static Collection<Worker> getCollection() {return AbstractCommand.collection;}
    public static Map<Integer, Worker> getMapCollection() {return AbstractCommand.mapCollection;}
    public static void setCollection(Collection<Worker> collection) {AbstractCommand.collection = collection;}
    public static void setMapCollection(Map<Integer, Worker> mapCollection) {AbstractCommand.mapCollection = mapCollection;}

    public AbstractCommand() {}
    public void setCommandName(String commandName) {this.commandName = commandName;}
    public String getCommandName() {return commandName;}
    public Worker executeWithReturn(){return null;}
    public Worker executeWithIntAndReturn(Integer number, Collection<Worker> collection, Map<Integer, Worker> mapCollection) {return null;}
    public void exit(){}
    public List<?> executeWithIntAndCollection(Integer number, Collection<Worker> collection, Map<Integer, Worker> mapCollection) {return null;}
    public List<?> executeWithCollection(Collection<Worker> collection, Map<Integer, Worker> mapCollection) {return null;}
    public List<Worker> executeWithArgsAndCollection(String argument, Collection<Worker> collection, Map<Integer, Worker> mapCollection) throws IOException{return null;}
    public List<?> executeWithoutArgs() {return null;}
    public void serverCommand(Collection<Worker> collection, Map<Integer, Worker> mapCollection) throws IOException {}
    public void executeScript() {}

    //public abstract Container<? extends AbstractCommand> createContainer(String[] args);

    public List<String> getStringList() {return stringList;}
}