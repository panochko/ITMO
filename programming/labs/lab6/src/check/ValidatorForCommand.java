package check;

import java.io.IOException;
import java.util.*;

import SEclasses.*;

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
    public static String checkExecuteScript(Scanner scanner, Set<String> set) {
        while (true) {
            System.out.print(Color.RESET + "Введите название скрипта: ");
            String fileName = scanner.nextLine().trim();
            if (fileName.trim().isEmpty()){
                System.out.println(Color.RED + "название скрипта не может быть пустым");
                continue;
            }
            try {
                if (set.contains(fileName))
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
    /**
     * validator for commands "add", "add_if_max", "add_if_min" for checking valid fields in executed scripts
     **/
    public static String checkNameAdd(String string) {
        try {
            if (string.matches("[A-Za-zА-Яа-я- ]+"))
                return string;
            else
                throw new IOException();
        } catch (IOException e) {
            System.out.println(Color.RED + "имя может содержать только буквы. измените первое поле");
        }
        return null;
    }
    public static Integer checkFirstCoordinateAdd(String string) {
        try {
            if (string.matches("\\d+"))
                return Integer.parseInt(string);
            else
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println(Color.RED + "первая координата может содержать только цифры. число должно быть целым. измените второе поле");
        }
        return null;
    }
    public static Float checkSecondCoordinateAdd(String string) {
        try {
            if (string.matches("\\d+") && Float.parseFloat(string) <= 274)
                return Float.parseFloat(string);
            else
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println(Color.RED + "вторая координата может содержать только цифры и должна быть не больше 274. число должно быть дробным. измените третье поле");
        }
        return null;
    }
    public static Integer checkSalaryAdd(String string) {
        try {
            if (string.matches("\\d+") && Integer.parseInt(string) > 0)
                return Integer.parseInt(string);
            else
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println(Color.RED + "зарплата не может быть пустой и не должна быть меньше 0. число должно быть целым. измените четвертое поле");
        }
        return null;
    }
    public static Position checkPositionAdd(String string) {
        try {
            return Position.valueOf(string.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println(Color.RED + "доступные должности: DEVELOPER LEAD_DEVELOPER BAKER MANAGER_OF_CLEANING\nизмените пятое поле");
        }
        return null;
    }
    public static Status checkStatusAdd(String string) {
        try {
            return Status.valueOf(string.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println(Color.RED + "доступные статусы: FIRED HIRED RECOMMENDED_FOR_PROMOTION REGULAR PROBATION\nизмените шестое поле");
        }
        return null;
    }
    public static Double checkWeightAdd(String string) {
        try {
            if (Double.parseDouble(string) > 0)
                return Double.parseDouble(string);
            else
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println(Color.RED + "вес - дробное число. измените седьмое поле");
        }
        return null;
    }
    public static Integer checkFirstCoordinateOfLocationAdd(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            System.out.println(Color.RED + "первая координата локации должна быть целым числом. измените восьмое поле");
        }
        return null;
    }
    public static Long checkSecondCoordinateOfLocationAdd(String string) {
        try {
            return Long.parseLong(string);
        } catch (NumberFormatException e) {
            System.out.println(Color.RED + "вторая координата локации должна быть целым числом. измените девятое поле");
        }
        return null;
    }
    public static Long checkThirdCoordinateOfLocationAdd(String string) {
        try {
            return Long.parseLong(string);
        } catch (NumberFormatException e) {
            System.out.println(Color.RED + "третья координата локации должна быть целым числом. измените десятое поле");
        }
        return null;
    }
    public static String checkNameOfLocationAdd(String string) {
        try {
            if (string.matches("[A-Za-zА-ЯА-я- ]+"))
                return string;
            else
                throw new IOException();
        } catch (IOException e) {
            System.out.println(Color.RED + "название локации может содержать только буквы. измените одиннадцатое поле");
        }
        return null;
    }
}
