package start;
import design.Color;
import server.ServerManager;
import java.io.IOException;
import java.sql.SQLException;

public class Server {
    public static String nameOfCollection;
    public static String dbUser;
    public static String dbPass;

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.println(Color.RED + "использование: java Server <коллекция> <логин_БД> <пароль_БД>");
            System.out.println(Color.RED + "доступные коллекции: ArrayList, TreeSet, LinkedHashMap, PriorityQueue");
            return;
        }
        nameOfCollection = args[0];
        dbUser = args[1];
        dbPass = args[2];
        if (!nameOfCollection.equals("ArrayList") && !nameOfCollection.equals("TreeSet") &&
                !nameOfCollection.equals("LinkedHashMap") && !nameOfCollection.equals("PriorityQueue")) {
            System.out.println(Color.RED + "доступные коллекции: ArrayList, TreeSet, LinkedHashMap, PriorityQueue");
            return;
        }
        Thread thread = new Thread(() -> {
            try {
                ServerManager.response(nameOfCollection, dbUser, dbPass);
            } catch (IOException e) {
                System.out.println(Color.RED + "ошибка подключения к серверу");
            } catch (ClassNotFoundException | SQLException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
    }
}