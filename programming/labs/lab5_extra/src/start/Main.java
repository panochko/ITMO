/**
 *@author Anton Panochko
 **/
package start;
import design.Color;

import java.io.IOException;

import static start.CommandManager.run;

public class Main {
    public static void main(String[] args) throws IOException {
        try {
            switch (args[0]) {
                case "ArrayList" -> run("ArrayList");
                case "TreeSet" -> run("TreeSet");
                case "LinkedHashMap" -> run("LinkedHashMap");
                case "PriorityQueue" -> run("PriorityQueue");
                default -> throw new IOException();
            }
        } catch (IOException e) {
            System.out.println(Color.RED + "доступные коллекции: ArrayList, TreeSet, LinkedHashMap, PriorityQueue");
        }
    }
}