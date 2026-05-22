package check;
import SEclasses.*;
import design.Color;

import java.util.List;
import java.util.Scanner;

public class ValidatorSE {

    public static int checkID(Scanner scanner, List<Integer> list) {
        while (true) {
            System.out.print(Color.RESET + "введите id элемента, который нужно удалить: ");
            String input = scanner.nextLine();
            if (input.trim().isEmpty()) {
                System.out.println(Color.RESET + "строка не может быть пустой");
                continue;
            }
            try {
                int id = Integer.parseInt(input);
                if (!list.contains(id)){
                    System.out.println(Color.RESET + "такого id нет");
                    continue;
                }
                if (id <= 0) {
                    System.out.println(Color.RESET + "id должен быть больше 0");
                    continue;
                }
                return id;
            } catch (NumberFormatException e) {
                System.out.println(Color.RESET + "это не целое число");
            }
        }
    }

    public static String checkName(Scanner scanner) {
        while (true) {
            System.out.print(Color.RESET + "введите имя работника: ");
            String input = scanner.nextLine();
            if (input.trim().isEmpty()) {
                System.out.println(Color.RESET + "имя не может быть пустым");
                continue;
            }
            String name = input.trim();
            if (!name.matches("[A-Za-zА-Яа-я- ]+")) {
                System.out.println(Color.RESET + "имя может содержать только буквы");
                continue;
            }
            return name;
        }
    }

    public static int checkFirstCoordinate(Scanner scanner) {
        while (true) {
            System.out.print(Color.RESET + "введите первую координату работника (целое число): ");
            String input = scanner.nextLine();
            if (input.trim().isEmpty()) {
                System.out.println(Color.RESET + "координата не может быть пустой");
                continue;
            }
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(Color.RESET + "число должно быть целым");
            }
        }
    }

    public static float checkSecondCoordinate(Scanner scanner) {
        while (true) {
            System.out.print(Color.RESET + "введите вторую координату работника (дробное число не больше 274): ");
            String input = scanner.nextLine();
            if (input.trim().isEmpty()) {
                System.out.println(Color.RESET + "координата не может быть пустой");
                continue;
            }
            try {
                float value = Float.parseFloat(input);
                if (value > 274) {
                    System.out.println(Color.RESET + "число должно быть не больше 274");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println(Color.RESET + "число должно быть дробным");
            }
        }
    }

    public static int checkSalary(Scanner scanner) {
        while (true) {
            System.out.print(Color.RESET + "введите заработную плату работника: ");
            String input = scanner.nextLine();
            if (input.trim().isEmpty()) {
                System.out.println(Color.RESET + "зарплата не может быть пустой");
                continue;
            }
            try {
                int salary = Integer.parseInt(input.trim());
                if (salary <= 0) {
                    System.out.println(Color.RESET + "заработная плата должна быть больше 0");
                    continue;
                }
                return salary;
            } catch (NumberFormatException e) {
                System.out.println(Color.RESET + "это не целое число");
            }
        }
    }

    public static Position checkPosition(Scanner scanner) {
        while (true) {
            System.out.print(Color.RESET + "введите должность работника: ");
            String input = scanner.nextLine();
            if (input.trim().isEmpty()) {
                System.out.println(Color.RESET + "должность не может быть пустой");
                continue;
            }
            try {
                return Position.valueOf(input.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println(Color.RESET + "доступные должности: DEVELOPER LEAD_DEVELOPER BAKER MANAGER_OF_CLEANING");
            }
        }
    }

    public static Status checkStatus(Scanner scanner) {
        while (true) {
            System.out.print(Color.RESET + "введите статус работника: ");
            String input = scanner.nextLine();
            if (input.trim().isEmpty()) {
                System.out.println(Color.RESET + "статус не может быть пустым");
                continue;
            }
            try {
                return Status.valueOf(input.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println(Color.RESET + "доступные статусы: FIRED HIRED RECOMMENDED_FOR_PROMOTION REGULAR PROBATION");
            }
        }
    }

    public static double checkWeight(Scanner scanner) {
        while (true) {
            System.out.print(Color.RESET + "введите вес работника (дробное число больше 0): ");
            String input = scanner.nextLine();
            if (input.trim().isEmpty()) {
                System.out.println(Color.RESET + "вес не может быть пустым");
                continue;
            }
            try {
                double weight = Double.parseDouble(input);
                if (weight <= 0) {
                    System.out.println(Color.RESET + "число должно быть больше 0");
                    continue;
                }
                return weight;
            } catch (NumberFormatException e) {
                System.out.println(Color.RESET + "число должно быть дробным");
            }
        }
    }

    public static int checkFirstCoordinateOfLocation(Scanner scanner) {
        while (true) {
            System.out.print(Color.RESET + "введите первую координату города (целое число): ");
            String input = scanner.nextLine();
            if (input.trim().isEmpty()) {
                System.out.println(Color.RESET + "координата не может быть пустой");
                continue;
            }
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(Color.RESET + "число должно быть целым");
            }
        }
    }

    public static long checkSecondCoordinateOfLocation(Scanner scanner) {
        while (true) {
            System.out.print(Color.RESET + "введите вторую координату города (целое число): ");
            String input = scanner.nextLine();
            if (input.trim().isEmpty()) {
                System.out.println(Color.RESET + "координата не может быть пустой");
                continue;
            }
            try {
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println(Color.RESET + "число должно быть целым");
            }
        }
    }

    public static long checkThirdCoordinateOfLocation(Scanner scanner) {
        while (true) {
            System.out.print(Color.RESET + "введите третью координату города (целое число): ");
            String input = scanner.nextLine();
            if (input.trim().isEmpty()) {
                System.out.println(Color.RESET + "координата не может быть пустой");
                continue;
            }
            try {
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println(Color.RESET + "число должно быть целым");
            }
        }
    }

    public static String checkNameOfLocation(Scanner scanner) {
        while (true) {
            System.out.print(Color.RESET + "введите название города: ");
            String input = scanner.nextLine();
            if (input.trim().isEmpty()) {
                System.out.println(Color.RESET + "название города не может быть пустым");
                continue;
            }
            String name = input.trim();
            if (!name.matches("[A-Za-zА-Яа-я- ]+")) {
                System.out.println(Color.RESET + "название города может содержать только буквы");
                continue;
            }
            return name;
        }
    }
}
