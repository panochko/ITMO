package client;

import SEclasses.Worker;
import commands.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
public class Container<T extends AbstractCommand> implements Serializable {
    @Serial
    private static final long serialVersionUID = -19721816641471083L;
    private String[] userArray;

    private T command;
    private Worker worker;
    private String nameOfCommand;
    private String[] extraArguments;
    private Integer[] numbers;
    private List<String> list;
    private List<Integer> integerList;
    public Container(T command, Worker worker, String[] userArray) {
        this.command = command;
        this.worker = worker;
        this.userArray = userArray;
    }
    public Container(T command) {
        this.command = command;
    }
    public static <T extends AbstractCommand> Container<T> withStringList(T command, List<String> list, String[] userArray) {
        Container<T> container = new Container<>(command);
        container.list = list;
        container.userArray = userArray;
        return container;
    }
    public static <T extends AbstractCommand> Container<T> withIntegerList(T command, List<Integer> integerList, String[] userArray) {
        Container<T> container = new Container<>(command);
        container.integerList = integerList;
        container.userArray = userArray;
        return container;
    }
    public Container(T command, String[] userArray) {
        this.command = command;
        this.userArray = userArray;
    }
    public static <T extends AbstractCommand> Container<T> withWorkerAndIntegerList(T command, Worker worker, List<Integer> list, String[] userArray) {
        Container<T> c = new Container<>(command, worker, userArray);
        c.setIntegerList(list);
        return c;
    }
    public T getCommand() {return command;}
    public void setCommand(T abstractCommand) {this.command = abstractCommand;}
    public String getNameOfCommand() {return nameOfCommand;}
    public void setNameOfCommand(String nameOfCommand) {this.nameOfCommand = nameOfCommand;}
    public String[] getExtraArguments() {return extraArguments;}
    public void setExtraArguments(String[] extraArguments) {this.extraArguments = extraArguments;}
    public Integer[] getNumbers() {return numbers;}
    public void setNumbers(Integer[] numbers) {this.numbers = numbers;}
    public Worker getWorker() {return worker;}
    public void setWorker(Worker worker){this.worker = worker;}
    public List<String> getList() {return list;}
    public void setList(List<String> list) {this.list = list;}
    public List<Integer> getIntegerList() {return integerList;}
    public void setIntegerList(List<Integer> integerList) {this.integerList = integerList;}
    public String[] getUserArray() {return this.userArray;}
}