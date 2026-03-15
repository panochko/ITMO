package commands;

import check.ValidatorForCommand;
import com.google.gson.*;
import SEclasses.*;
import design.Color;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public final class Save extends AbstractCommand{
    @Override
    public void execute(String arguments){
        if (arguments.equals("ArrayList") || arguments.equals("TreeSet") || arguments.equals("PriorityQueue")) {
            Scanner scanner = new Scanner(System.in);
            System.out.println(Color.RESET + "В директории src/start файл для сохранения коллекции - FileWorker.json");
            String fileName = ValidatorForCommand.checkSave(scanner);
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("src/start/"+fileName))) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String jsonSet = gson.toJson(collection);
                bufferedWriter.write(jsonSet);
                System.out.println(Color.RESET + "Коллекция сохранена!");
            } catch (IOException e) {
                System.out.println(Color.RED + "такого файла нет");
            }
        }
        else {
            Scanner scanner = new Scanner(System.in);
            System.out.println(Color.RESET + "В директории src/start файл для сохранения коллекции - FileWorker.json");
            String fileName = ValidatorForCommand.checkSave(scanner);
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("src/start/"+fileName))) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String jsonSet = gson.toJson(mapCollection);
                bufferedWriter.write(jsonSet);
                System.out.println(Color.RESET + "Коллекция сохранена!");
            } catch (IOException e) {
                System.out.println(Color.RED + "такого файла нет");
            }
        }
    }
}
