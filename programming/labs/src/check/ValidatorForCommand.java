package check;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import SEclasses.Worker;
import design.Color;

public class ValidatorForCommand {

    public static int checkUpdate(Scanner scanner, Map<Integer, Worker> map){
        while (true) {
            System.out.print(Color.RESET + "Введите id работника, у которого хотите поменять параметры: ");
            String input = scanner.nextLine();
            if (input.trim().isEmpty()) {
                System.out.println(Color.RED + "id не может быть пустым");
                continue;
            }
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(Color.RED + "доступные id: "+map.keySet());
            }
        }
    }
    public static String checkExecuteScript(Scanner scanner, List<String> list) {
        while (true) {
            System.out.print(Color.RESET + "Введите название скрипта: "); // введите путь до файла: src/scanner.nextLine()
            String fileName = scanner.nextLine();
            if (fileName.trim().isEmpty()){
                System.out.println(Color.RED + "название скрипта не может быть пустым");
                continue;
            }
            try {
                if (list.contains(fileName))
                    return fileName;
                else
                    throw new IOException();
            } catch (IOException e) {
                System.out.println(Color.RED + "нет такого скрипта");
            }
        }
    }
    public static String checkSave(Scanner scanner){
        while (true) {
            System.out.print(Color.RESET + "Введите название файла: ");
            String fileName = scanner.nextLine();
            if (fileName.trim().isEmpty()){
                System.out.println(Color.RED + "название файла не может быть пустым");
                continue;
            }
            try {
                if (fileName.equals("FileWorker.json"))
                    return fileName;
                else
                    throw new IOException();
            } catch (IOException e) {
                System.out.println(Color.RED + "файл для сохранения коллекции - FileWorker.json");
            }
        }
    }
}
