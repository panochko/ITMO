package commands;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;
import SEclasses.*;
import design.Color;

public final class Help extends AbstractCommand{
    @Override
    public void execute(String arguments) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(Color.RESET + "help : вывести справку по доступным командам");
        while (true) {
            System.out.print(Color.RESET + "=> ");
            try {
                if (Objects.equals(scanner.nextLine(), "help"))
                    break;
                else
                    throw new IOException();
            } catch (IOException e) {
                System.out.println(Color.RED + "некорректный ввод, введите help");
            }
        }
        System.out.println("\tinfo : вывести в стандартный поток вывода информацию о коллекции");
        System.out.println("\tshow : вывести в стандартный поток вывода все элементы коллекции в строковом представлении");
        System.out.println("\tadd worker : добавить нового работника в коллекцию");
        System.out.println("\tupdate id worker : обновить значение элемента коллекции, id которого равен заданному");
        System.out.println("\tremove_by_id id : удалить элемент из коллекции по его id");
        System.out.println("\tclear : очистить коллекцию");
        System.out.println("\tsave : сохранить коллекцию в файл");
        System.out.println("\texecute_script file_name : считать и исполнить скрипт из указанного файла");
        System.out.println("\texit : завершить программу (без сохранения в файл)");
        System.out.println("\tadd_if_max worker : добавить новый элемент в коллекцию, если его salary превышает salary наибольшего элемента этой коллекции");
        System.out.println("\tadd_if_min worker : добавить новый элемент в коллекцию, если его salary меньше, чем у наименьшего элемента этой коллекции");
        System.out.println("\tremove_greater worker(s) : удалить из коллекции работников, у кого salary превышает определенной суммы");
        System.out.println("\tmin_by_coordinates : вывести любой объект из коллекции, значение поля coordinates которого является минимальным");
        System.out.println("\tfilter_contains_name name : вывести элементы, значение поля name которых содержит заданную подстроку");
        System.out.println("\tfilter_starts_with_name name : вывести элементы, значение поля name которых начинается с заданной подстроки");
    }
}
