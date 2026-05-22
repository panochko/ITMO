package client;

import SEclasses.Worker;
import commands.*;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings({"unused", "unchecked"})
public class ClientNetwork {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 60000;
    private static final ByteBuffer buffer = ByteBuffer.allocate(10000);
    private static DatagramSocket socket;
    private static InetAddress serverAddress;
    private static String currentUser;
    private static String currentPassword;
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    static {
        try {
            socket = new DatagramSocket();
            serverAddress = InetAddress.getByName(SERVER_ADDRESS);
        } catch (Exception e) {
            System.err.println("Ошибка инициализации сети");
        }
    }
    private ClientNetwork() {
    }
    public static boolean authorize(String username, String password) throws IOException, ClassNotFoundException {
        currentUser = username;
        currentPassword = password;
        Container<Help> container = new Container<>(new Help("help"), new String[]{username, password});
        return sendRequest(container) != null;
    }
    public static List<Worker> showWorkers() throws IOException, ClassNotFoundException {
        Container<Show> container = new Container<>(new Show("show"), new String[]{currentUser, currentPassword});
        Object response = sendRequest(container);
        return response instanceof List<?> ? (List<Worker>) response : new ArrayList<>();
    }
    public static String addWorker(Worker worker) throws IOException, ClassNotFoundException {
        Add add = new Add("add");
        Container<Add> container = new Container<>(add, worker, new String[]{currentUser, currentPassword});
        Object response = sendRequest(container);
        return response != null ? response.toString() : "Ошибка добавления";
    }
    public static String updateWorker(int id, Worker worker) throws IOException, ClassNotFoundException {
        Update update = new Update("update " + id);
        List<Integer> idList = List.of(id);
        Container<Update> container = Container.withWorkerAndIntegerList(update, worker, idList, new String[]{currentUser, currentPassword});
        Object response = sendRequest(container);
        return response != null ? response.toString() : "Ошибка обновления";
    }

    public static String deleteWorker(int id) throws IOException, ClassNotFoundException {
        RemoveByID removeByID = new RemoveByID("remove_by_id " + id);
        List<Integer> idList = List.of(id);
        Container<RemoveByID> container = Container.withIntegerList(removeByID, idList, new String[]{currentUser, currentPassword});
        Object response = sendRequest(container);
        return response != null ? response.toString() : "Ошибка удаления";
    }
    public static String clearWorkers() throws IOException, ClassNotFoundException {
        Clear clear = new Clear("clear");
        Container<Clear> container = new Container<>(clear, new String[]{currentUser, currentPassword});
        Object response = sendRequest(container);
        return response != null ? response.toString() : "Ошибка очистки";
    }
    public static List<Worker> filterContainsName(String name) throws IOException, ClassNotFoundException {
        FilterContainsName filter = new FilterContainsName("filter_contains_name");
        List<String> nameList = List.of(name);
        Container<FilterContainsName> container = Container.withStringList(filter, nameList, new String[]{currentUser, currentPassword});
        Object response = sendRequest(container);
        return response instanceof List<?> ? (List<Worker>) response : new ArrayList<>();
    }
    public static List<Worker> filterStartsWithName(String name) throws IOException, ClassNotFoundException {
        FilterStartsWithName filter = new FilterStartsWithName("filter_starts_with_name");
        List<String> nameList = List.of(name);
        Container<FilterStartsWithName> container = Container.withStringList(filter, nameList, new String[]{currentUser, currentPassword});
        Object response = sendRequest(container);
        return response instanceof List<?> ? (List<Worker>) response : new ArrayList<>();
    }
    public static String removeGreater(int id) throws IOException, ClassNotFoundException {
        RemoveGreater removeGreater = new RemoveGreater("remove_greater");
        List<Integer> idList = List.of(id);
        Container<RemoveGreater> container = Container.withIntegerList(removeGreater, idList, new String[]{currentUser, currentPassword});
        Object response = sendRequest(container);
        return response != null ? response.toString() : "Ошибка удаления";
    }
    public static List<Worker> minByCoordinates() throws IOException, ClassNotFoundException {
        MinByCoordinates minByCoordinates = new MinByCoordinates("min_by_coordinates");
        Container<MinByCoordinates> container = new Container<>(minByCoordinates, new String[]{currentUser, currentPassword});
        Object response = sendRequest(container);
        return response instanceof List<?> ? (List<Worker>) response : new ArrayList<>();
    }
    public static List<String> info() throws IOException, ClassNotFoundException {
        Info info = new Info("info");
        Container<Info> container = new Container<>(info, new String[]{currentUser, currentPassword});
        Object response = sendRequest(container);
        return response instanceof List<?> ? (List<String>) response : new ArrayList<>();
    }
    private static Object sendRequest(Container<?> container) throws IOException, ClassNotFoundException {
        CompletableFuture<Object> future = new CompletableFuture<>();
        executor.submit(() -> {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(byteArrayOutputStream);
                oos.writeObject(container);
                oos.flush();
                byte[] data = byteArrayOutputStream.toByteArray();
                DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress, SERVER_PORT);
                socket.send(packet);
                buffer.clear();
                DatagramPacket responsePacket = new DatagramPacket(buffer.array(), buffer.capacity());
                socket.setSoTimeout(10000);
                socket.receive(responsePacket);
                byte[] responseData = new byte[responsePacket.getLength()];
                System.arraycopy(buffer.array(), 0, responseData, 0, responsePacket.getLength());
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(responseData);
                ObjectInputStream ois = new ObjectInputStream(byteArrayInputStream);
                Object response = ois.readObject();
                future.complete(response);
            } catch (IOException e) {
                System.err.println("Ошибка ввода-вывода: " + e.getMessage());
                future.completeExceptionally(e);
            } catch (ClassNotFoundException e) {
                System.err.println("Класс не найден: " + e.getMessage());
                future.completeExceptionally(e);
            } catch (Exception e) {
                System.err.println("Неожиданная ошибка: " + e.getMessage());
                future.completeExceptionally(e);
            }
        });
        return future.join();
    }
    public static void close() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        executor.shutdown();
    }
}