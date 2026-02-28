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
        Scanner scanner = new Scanner(System.in);
        System.out.println(Color.RESET + "В директории src файл для сохранения коллекции - FileWorker.json");
        String fileName = ValidatorForCommand.checkSave(scanner);
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("src/"+fileName))) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonSet = gson.toJson(set);
            bufferedWriter.write(jsonSet);
            System.out.println(Color.RESET + "Коллекция сохранена!");
        } catch (IOException e) {
            System.out.println(Color.RED + "такого файла нет");
        }
    }
}
