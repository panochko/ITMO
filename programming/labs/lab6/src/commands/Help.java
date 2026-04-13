package commands;

import java.io.Serial;
import java.util.List;

public final class Help extends AbstractCommand{
    @Serial
    private static final long serialVersionUID = -824058256698177048L;
    public Help(String commandName) {
        super(commandName);
    }
    @Override
    public List<String> executeWithoutArgs() {
        stringList.clear();
        stringList.add("\thelp : доступные команды");
        stringList.add("\tinfo : вывести в стандартный поток вывода информацию о коллекции");
        stringList.add("\tshow : вывести в стандартный поток вывода все элементы коллекции в строковом представлении");
        stringList.add("\tadd : добавить нового работника в коллекцию");
        stringList.add("\tupdate id : обновить значение элемента коллекции, id которого равен заданному");
        stringList.add("\tremove_by_id id : удалить элемент из коллекции по его id");
        stringList.add("\tclear : очистить коллекцию");
        stringList.add("\texecute_script : считать и исполнить скрипт из указанного файла");
        stringList.add("\texit : завершить программу");
        stringList.add("\tadd_if_max : добавить новый элемент в коллекцию, если его salary превышает salary наибольшего элемента этой коллекции");
        stringList.add("\tadd_if_min : добавить новый элемент в коллекцию, если его salary меньше, чем у наименьшего элемента этой коллекции");
        stringList.add("\tremove_greater salary : удалить из коллекции работников, у кого salary превышает определенной суммы");
        stringList.add("\tmin_by_coordinates : вывести любой объект из коллекции, значение поля coordinates которого является минимальным");
        stringList.add("\tfilter_contains_name name : вывести элементы, значение поля name которых содержит заданную подстроку");
        stringList.add("\tfilter_starts_with_name name : вывести элементы, значение поля name которых начинается с заданной подстроки");
        return stringList;
    }
}