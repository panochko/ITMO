package server;

import SEclasses.Worker;
import client.Container;
import commands.*;
import design.Color;
import java.io.*;
import java.util.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerManager {
    private final static ByteBuffer buffer = ByteBuffer.allocate(10000);
    public static final Logger logger = LoggerFactory.getLogger(ServerManager.class);
    public static void response(String nameOfCollection) throws IOException, ClassNotFoundException {
        Map<String, AbstractCommand> commandMap = new HashMap<>();
        commandMap.put("help", new Help("help"));
        commandMap.put("add", new Add("add"));
        commandMap.put("add_if_max", new AddIfMax("add_if_max"));
        commandMap.put("add_if_min", new AddIfMin("add_if_min"));
        commandMap.put("filter_contains_name", new FilterContainsName("filter_contains_name"));
        commandMap.put("filter_starts_with_name", new FilterStartsWithName("filter_starts_with_name"));
        commandMap.put("remove_greater", new RemoveGreater("remove_greater"));
        commandMap.put("remove_by_id", new RemoveByID("remove_by_id"));
        commandMap.put("info", new Info("info"));
        commandMap.put("show", new Show("show"));
        commandMap.put("min_by_coordinates", new MinByCoordinates("min_by_coordinates"));
        commandMap.put("exit", new Exit("exit"));
        commandMap.put("update", new Update("update"));
        commandMap.put("clear", new Clear("clear"));
        commandMap.put("execute_script", new ExecuteScript("execute_script"));
        InetAddress inetAddress = InetAddress.getByName("localhost");
        int port = 60000;
        logger.info("Server started on {}:{}", inetAddress, port);
        switch (nameOfCollection) {
            case "TreeSet" -> AbstractCommand.collection = new TreeSet<>();
            case "ArrayList" -> AbstractCommand.collection = new ArrayList<>();
            case "PriorityQueue" -> AbstractCommand.collection = new PriorityQueue<>();
            default -> AbstractCommand.mapCollection = new LinkedHashMap<>();
        }
        try {
            Loading loader = new Loading();
            loader.serverCommand(AbstractCommand.collection, AbstractCommand.mapCollection);
            logger.info("Collection loaded from file");
        } catch (Exception e) {
            logger.warn("Could not load collection (starting with empty): {}", e.getMessage());
        }
        logger.info("Loading data into {}", nameOfCollection);
        try (DatagramSocket datagramSocket = new DatagramSocket(port, inetAddress)) {
            while (true) {
                buffer.clear();
                DatagramPacket datagramPacket = new DatagramPacket(buffer.array(), buffer.array().length);
                datagramSocket.receive(datagramPacket);
                InetAddress clientAddress = datagramPacket.getAddress();
                int clientPort = datagramPacket.getPort();
                //System.out.println("Получен пакет от " + clientAddress + ":" + clientPort);
                logger.info("Received a package from {}:{}", clientAddress, clientPort);
                DatagramPacket datagramPacketForSending;
                byte[] receivedBytes = new byte[datagramPacket.getLength()];
                System.arraycopy(buffer.array(), 0, receivedBytes, 0, datagramPacket.getLength());
                try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(receivedBytes);
                     ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
                    Object obj = objectInputStream.readObject();
                    if (!(obj instanceof Container<? extends AbstractCommand> container)) {
                        String errorMsg = Color.RED + "Получен объект типа " + obj.getClass() + ", ожидался Container";
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        ObjectOutputStream oos = new ObjectOutputStream(byteArrayOutputStream);
                        oos.writeObject(errorMsg);
                        byte[] errorBytes = byteArrayOutputStream.toByteArray();
                        datagramPacketForSending = new DatagramPacket(errorBytes, errorBytes.length, clientAddress, clientPort);
                        datagramSocket.send(datagramPacketForSending);
                        continue;
                    }
                    String commandName = container.getCommand().getCommandName();
                    //System.out.println("Получена команда: " + commandName);
                    logger.info("Received command: {}", commandName);
                    logger.info("Command {} response has been sent to client {}", commandName, datagramPacket.getAddress());
                    if (commandName.matches("filter_contains_name [A-Za-zА-Яа-я ,-.]+")) {
                        FilterContainsName filterContainsName = (FilterContainsName) container.getCommand();
                        List<String> list = container.getList();
                        String pattern = list.getFirst();
                        for (Worker worker : filterContainsName.executeWithArgsAndCollection(pattern, AbstractCommand.collection, AbstractCommand.mapCollection)) {
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                            objectOutputStream.writeObject(worker.toString());
                            byte[] containerBytes = byteArrayOutputStream.toByteArray();
                            datagramPacketForSending = new DatagramPacket(containerBytes, containerBytes.length, clientAddress, clientPort);
                            datagramSocket.send(datagramPacketForSending);
                        }
                        //System.out.println("Ответ " + commandName + " отправлен клиенту " + clientAddress + ":" + clientPort);s());
                    }
                    if (commandName.matches("filter_starts_with_name [A-Za-zА-Яа-я ,-.]+")) {
                        FilterStartsWithName filterStartsWithName = (FilterStartsWithName) container.getCommand();
                        List<String> list = container.getList();
                        String pattern = list.getFirst();
                        for (Worker worker : filterStartsWithName.executeWithArgsAndCollection(pattern, AbstractCommand.collection, AbstractCommand.mapCollection)) {
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                            objectOutputStream.writeObject(worker.toString());
                            byte[] containerBytes = byteArrayOutputStream.toByteArray();
                            datagramPacketForSending = new DatagramPacket(containerBytes, containerBytes.length, clientAddress, clientPort);
                            datagramSocket.send(datagramPacketForSending);
                        }
                        //System.out.println("Ответ " + commandName + " отправлен клиенту " + clientAddress + ":" + clientPort);
                    }
                    if (commandName.matches("remove_by_id [0-9]+")) {
                        RemoveByID removeByID = (RemoveByID) container.getCommand();
                        List<Integer> integerList = container.getIntegerList();
                        Integer number = integerList.getFirst();
                        StringBuilder response = new StringBuilder();
                        if (removeByID.executeWithIntAndReturn(number, AbstractCommand.collection, AbstractCommand.mapCollection) != null)
                            response.append(Color.BLUE + "работник с id ").append(number).append(Color.BLUE + " удалён");
                        else
                            response.append(Color.BLUE + "работника с id ").append(number).append(Color.BLUE + " не существует");
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                        objectOutputStream.writeObject(response);
                        byte[] containerBytes = byteArrayOutputStream.toByteArray();
                        datagramPacketForSending = new DatagramPacket(containerBytes, containerBytes.length, clientAddress, clientPort);
                        datagramSocket.send(datagramPacketForSending);
                        //System.out.println("Ответ " + commandName + " отправлен клиенту " + clientAddress + ":" + clientPort);
                    }
                    if (commandName.matches("update [0-9]+")) {
                        Update update = (Update) container.getCommand();
                        List<Integer> integerList = container.getIntegerList();
                        Integer number = integerList.getFirst();
                        StringBuilder response = new StringBuilder();
                        if (update.executeWithIntAndReturn(number, AbstractCommand.collection, AbstractCommand.mapCollection) != null)
                            response.append(Color.BLUE + "поля работника с id ").append(number).append(Color.BLUE + " обновлены");
                        else
                            response.append(Color.BLUE + "работника с id ").append(number).append(Color.BLUE + " не существует");
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                        objectOutputStream.writeObject(response);
                        byte[] containerBytes = byteArrayOutputStream.toByteArray();
                        datagramPacketForSending = new DatagramPacket(containerBytes, containerBytes.length, clientAddress, clientPort);
                        datagramSocket.send(datagramPacketForSending);
                        //System.out.println("Ответ " + commandName + " отправлен клиенту " + clientAddress + ":" + clientPort);
                    }
                    if (commandName.matches("remove_greater [0-9]+")) {
                        RemoveGreater removeGreater = (RemoveGreater) container.getCommand();
                        List<Integer> integerList = container.getIntegerList();
                        Integer number = integerList.getFirst();
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                        objectOutputStream.writeObject(removeGreater.executeWithIntAndCollection(number, AbstractCommand.collection, AbstractCommand.mapCollection));
                        byte[] containerBytes = byteArrayOutputStream.toByteArray();
                        datagramPacketForSending = new DatagramPacket(containerBytes, containerBytes.length, clientAddress, clientPort);
                        datagramSocket.send(datagramPacketForSending);
                        //System.out.println("Ответ " + commandName + " отправлен клиенту " + clientAddress + ":" + clientPort);
                    }
                    if (commandName.matches("execute_script( [A-Za-zА-Яа-я0-9_.]*)?")) {
                        ExecuteScript executeScript = (ExecuteScript) container.getCommand();
                        List<String> list = container.getList();
                        List<String> listToClient = new ArrayList<>();
                        for (String command : list) {
                                if (command.matches("add [0-9A-Za-z-,]+")) {
                                    Worker worker = executeScript.parseAdd(command, AbstractCommand.collection, AbstractCommand.mapCollection);
                                    if (worker != null && AbstractCommand.mapCollection == null) {
                                        Worker.resetGenerator(AbstractCommand.collection);
                                        worker.setId();
                                        AbstractCommand.collection.add(worker);
                                        listToClient.add(Color.BLUE + "работник с id " + worker.getId() + " добавлен в коллекцию");
                                    }
                                    if (worker != null && AbstractCommand.collection == null) {
                                        Worker.resetGenerator(AbstractCommand.mapCollection);
                                        worker.setId();
                                        AbstractCommand.mapCollection.put(worker.getId(), worker);
                                        listToClient.add(Color.BLUE + "работник с id " + worker.getId() + " добавлен в коллекцию");
                                    }
                                }
                                if (command.matches("filter_contains_name [A-Za-zА-Яа-я ,-.]+")) {
                                    String[] parts = command.split(" ", 2);
                                    FilterContainsName filterContainsName = (FilterContainsName) commandMap.get(parts[0]);
                                    for (Worker worker : filterContainsName.executeWithArgsAndCollection(parts[1], AbstractCommand.collection, AbstractCommand.mapCollection))
                                        listToClient.add(worker.toString());

                                }
                                if (command.matches("filter_starts_with_name [A-Za-zА-Яа-я ,-.]+")) {
                                    String[] parts = command.split(" ", 2);
                                    FilterStartsWithName filterStartsWithName = (FilterStartsWithName) commandMap.get(parts[0]);
                                    for (Worker worker : filterStartsWithName.executeWithArgsAndCollection(parts[1], AbstractCommand.collection, AbstractCommand.mapCollection))
                                        listToClient.add(worker.toString());
                                }
                                if (command.matches("remove_by_id [0-9]+")) {
                                    String[] parts = command.split(" ", 2);
                                    RemoveByID removeByID = (RemoveByID) commandMap.get(parts[0]);
                                    Integer number = Integer.parseInt(parts[1]);
                                    if (removeByID.executeWithIntAndReturn(number, AbstractCommand.collection, AbstractCommand.mapCollection) != null)
                                        listToClient.add(Color.BLUE + "работник с id " + number + " удалён");
                                    else
                                        listToClient.add(Color.BLUE + "работника с id " + number + " не существует");
                                }
                                if (command.matches("update [0-9]+")) {
                                    String[] parts = command.split(" ", 2);
                                    Update update = (Update) commandMap.get(parts[0]);
                                    Integer number = Integer.parseInt(parts[1]);
                                    if (update.executeWithIntAndReturn(number, AbstractCommand.collection, AbstractCommand.mapCollection) != null)
                                        listToClient.add(Color.BLUE + "поля работника с id " + number + " обновлены");
                                    else
                                        listToClient.add(Color.BLUE + "работника с id " + number + " не существует");
                                }
                                if (command.matches("remove_greater [0-9]+")) {
                                    String[] parts = command.split(" ", 2);
                                    RemoveGreater removeGreater = (RemoveGreater) commandMap.get(parts[0]);
                                    Integer number = Integer.parseInt(parts[1]);
                                    listToClient.add(removeGreater.executeWithIntAndCollection(number, AbstractCommand.collection, AbstractCommand.mapCollection).toString());
                                }
                                if (command.matches("add_if_max [0-9A-Za-z-,]+")) {
                                    try {
                                        Worker worker = executeScript.parseAddIfMax(command, AbstractCommand.collection, AbstractCommand.mapCollection);
                                        if (worker != null && AbstractCommand.mapCollection == null) {
                                            Worker.resetGenerator(AbstractCommand.collection);
                                            worker.setId();
                                            AbstractCommand.collection.add(worker);
                                            listToClient.add(Color.BLUE + "работник с id " + worker.getId() + " добавлен в коллекцию");
                                        }
                                        if (worker != null && AbstractCommand.collection == null) {
                                            Worker.resetGenerator(AbstractCommand.mapCollection);
                                            worker.setId();
                                            AbstractCommand.mapCollection.put(worker.getId(), worker);
                                            listToClient.add(Color.BLUE + "работник с id " + worker.getId() + " добавлен в коллекцию");
                                        }
                                    } catch (NoSuchElementException e) {
                                        listToClient.add(Color.GREY + "команда " + command + " не выполнена, т.к. коллекция пуста");
                                    }
                                }
                                if (command.matches("add_if_min [0-9A-Za-z-,]+")) {
                                    try {
                                        Worker worker = executeScript.parseAddIfMin(command, AbstractCommand.collection, AbstractCommand.mapCollection);
                                        if (worker != null && AbstractCommand.mapCollection == null) {
                                            Worker.resetGenerator(AbstractCommand.collection);
                                            worker.setId();
                                            AbstractCommand.collection.add(worker);
                                            listToClient.add(Color.BLUE + "работник с id " + worker.getId() + " добавлен в коллекцию");
                                        }
                                        if (worker != null && AbstractCommand.collection == null) {
                                            Worker.resetGenerator(AbstractCommand.mapCollection);
                                            worker.setId();
                                            AbstractCommand.mapCollection.put(worker.getId(), worker);
                                            listToClient.add(Color.BLUE + "работник с id " + worker.getId() + " добавлен в коллекцию");
                                        }
                                    } catch (NoSuchElementException e) {
                                        listToClient.add(Color.GREY + "команда " + command + " не выполнена, т.к. коллекция пуста");
                                    }
                                }
                                else switch (command) {
                                    case "clear" -> {
                                        Clear clear = (Clear) commandMap.get(command);
                                        List<String> stringList = clear.executeWithCollection(AbstractCommand.collection, AbstractCommand.mapCollection);
                                        String response = Color.BLUE + stringList.getFirst();
                                        clear.executeWithCollection(AbstractCommand.collection, AbstractCommand.mapCollection);
                                        listToClient.add(response);
                                    }
                                    case "help" -> {
                                        Help help = (Help) commandMap.get(command);
                                        listToClient.addAll(help.executeWithoutArgs());
                                    }
                                    case "min_by_coordinates" -> {
                                        try {
                                            MinByCoordinates minByCoordinates = (MinByCoordinates) commandMap.get(command);
                                            listToClient.add(minByCoordinates.executeWithCollection(AbstractCommand.collection, AbstractCommand.mapCollection).toString());
                                        } catch (NoSuchElementException e) {
                                            listToClient.add(Color.GREY + "команда " + command + " не выполнена, т.к. коллекция пуста");

                                        }
                                    }
                                    case "info" -> {
                                        Info info = (Info) commandMap.get(command);
                                        listToClient.add(info.executeWithCollection(AbstractCommand.collection, AbstractCommand.mapCollection).toString());
                                    }
                                    case "show" -> {
                                        Show show = (Show) commandMap.get(command);
                                        listToClient.add(show.executeWithCollection(AbstractCommand.collection, AbstractCommand.mapCollection).toString());
                                    }
                                }
                                //System.out.println("Ответ " + commandName + " отправлен клиенту " + clientAddress + ":" + clientPort);
                        }
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                        objectOutputStream.writeObject(listToClient);
                        byte[] containerBytes = byteArrayOutputStream.toByteArray();
                        datagramPacketForSending = new DatagramPacket(containerBytes, containerBytes.length, clientAddress, clientPort);
                        datagramSocket.send(datagramPacketForSending);
                    }
                    else switch (commandName) {
                        case "add" -> {
                            if (AbstractCommand.collection != null) {
                                Worker.resetGenerator(AbstractCommand.collection);
                                Worker worker = container.getWorker();
                                worker.setId();
                                AbstractCommand.collection.add(worker);
                                String response = Color.BLUE + "работник с id " + worker.getId() + " добавлен в коллекцию";
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                                objectOutputStream.writeObject(response);
                                byte[] containerBytes = byteArrayOutputStream.toByteArray();
                                datagramPacketForSending = new DatagramPacket(containerBytes, containerBytes.length, clientAddress, clientPort);
                            } else {
                                Worker.resetGenerator(AbstractCommand.mapCollection);
                                Worker worker = container.getWorker();
                                worker.setId();
                                AbstractCommand.mapCollection.put(worker.getId(), worker);
                                String response = Color.BLUE + "работник с id " + worker.getId() + " добавлен в коллекцию";
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                                objectOutputStream.writeObject(response);
                                byte[] containerBytes = byteArrayOutputStream.toByteArray();
                                datagramPacketForSending = new DatagramPacket(containerBytes, containerBytes.length, clientAddress, clientPort);
                            }
                            datagramSocket.send(datagramPacketForSending);
                            //System.out.println("Ответ " + commandName + " отправлен клиенту " + clientAddress + ":" + clientPort);
                        }
                        case "add_if_max", "add_if_min" -> {
                            if (AbstractCommand.collection != null && container.getWorker() != null) {
                                Worker.resetGenerator(AbstractCommand.collection);
                                Worker worker = container.getWorker();
                                worker.setId();
                                AbstractCommand.collection.add(worker);
                                String response = Color.BLUE + "работник с id " + worker.getId() + " добавлен в коллекцию";
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                                objectOutputStream.writeObject(response);
                                byte[] containerBytes = byteArrayOutputStream.toByteArray();
                                datagramPacketForSending = new DatagramPacket(containerBytes, containerBytes.length, clientAddress, clientPort);
                                datagramSocket.send(datagramPacketForSending);
                                //System.out.println("Ответ " + commandName + " отправлен клиенту " + clientAddress + ":" + clientPort);
                            } else if (AbstractCommand.mapCollection != null && container.getWorker() != null) {
                                Worker.resetGenerator(AbstractCommand.mapCollection);
                                Worker worker = container.getWorker();
                                worker.setId();
                                AbstractCommand.mapCollection.put(worker.getId(), worker);
                                String response = Color.BLUE + "работник с id " + worker.getId() + " добавлен в коллекцию";
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                                objectOutputStream.writeObject(response);
                                byte[] containerBytes = byteArrayOutputStream.toByteArray();
                                datagramPacketForSending = new DatagramPacket(containerBytes, containerBytes.length, clientAddress, clientPort);
                                datagramSocket.send(datagramPacketForSending);
                                //System.out.println("Ответ " + commandName + " отправлен клиенту " + clientAddress + ":" + clientPort);
                            } else {
                                String response = Color.BLUE + "unfortunately, worker isn't added into collection";
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                                objectOutputStream.writeObject(response);
                                byte[] containerBytes = byteArrayOutputStream.toByteArray();
                                datagramPacketForSending = new DatagramPacket(containerBytes, containerBytes.length, clientAddress, clientPort);
                                datagramSocket.send(datagramPacketForSending);
                                //System.out.println("Ответ " + commandName + " отправлен клиенту " + clientAddress + ":" + clientPort);
                            }
                        }
                        case "clear" -> {
                            Clear clear = (Clear) container.getCommand();
                            List<String> stringList = clear.executeWithCollection(AbstractCommand.collection, AbstractCommand.mapCollection);
                            String response = Color.BLUE + stringList.getFirst();
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                            objectOutputStream.writeObject(response);
                            byte[] containerBytes = byteArrayOutputStream.toByteArray();
                            datagramPacketForSending = new DatagramPacket(containerBytes, containerBytes.length, clientAddress, clientPort);
                            datagramSocket.send(datagramPacketForSending);
                            //System.out.println("Ответ " + commandName + " отправлен клиенту " + clientAddress + ":" + clientPort);
                        }
                        case "help" -> {
                            Help help = (Help) container.getCommand();
                            List<String> helpList = help.executeWithoutArgs();
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                            objectOutputStream.writeObject(helpList);
                            byte[] containerBytes = byteArrayOutputStream.toByteArray();
                            datagramPacketForSending = new DatagramPacket(containerBytes, containerBytes.length, clientAddress, clientPort);
                            datagramSocket.send(datagramPacketForSending);
                            //System.out.println("Ответ " + commandName + " отправлен клиенту " + clientAddress + ":" + clientPort);
                        }
                        case "min_by_coordinates" -> {
                            try {
                                MinByCoordinates minByCoordinates = (MinByCoordinates) container.getCommand();
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                                objectOutputStream.writeObject(minByCoordinates.executeWithCollection(AbstractCommand.collection, AbstractCommand.mapCollection));
                                byte[] containerBytes = byteArrayOutputStream.toByteArray();
                                datagramPacketForSending = new DatagramPacket(containerBytes, containerBytes.length, clientAddress, clientPort);
                                datagramSocket.send(datagramPacketForSending);
                                //System.out.println("Ответ " + commandName + " отправлен клиенту " + clientAddress + ":" + clientPort);
                            } catch (NoSuchElementException e) {
                                String response = Color.GREY + "в коллекции нет элементов";
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                                objectOutputStream.writeObject(response);
                                byte[] containerBytes = byteArrayOutputStream.toByteArray();
                                datagramPacketForSending = new DatagramPacket(containerBytes, containerBytes.length, clientAddress, clientPort);
                                datagramSocket.send(datagramPacketForSending);
                                //System.out.println("Ответ " + commandName + " отправлен клиенту " + clientAddress + ":" + clientPort);
                            }
                        }
                        case "info" -> {
                            Info info = (Info) container.getCommand();
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                            objectOutputStream.writeObject(info.executeWithCollection(AbstractCommand.collection, AbstractCommand.mapCollection));
                            byte[] containerBytes = byteArrayOutputStream.toByteArray();
                            datagramPacketForSending = new DatagramPacket(containerBytes, containerBytes.length, clientAddress, clientPort);
                            datagramSocket.send(datagramPacketForSending);
                            //System.out.println("Ответ " + commandName + " отправлен клиенту " + clientAddress + ":" + clientPort);
                        }
                        case "show" -> {
                            Show show = (Show) container.getCommand();
                            for (Worker worker : show.executeWithCollection(AbstractCommand.collection, AbstractCommand.mapCollection)) {
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                                objectOutputStream.writeObject(worker.toString());
                                byte[] containerBytes = byteArrayOutputStream.toByteArray();
                                datagramPacketForSending = new DatagramPacket(containerBytes, containerBytes.length, clientAddress, clientPort);
                                datagramSocket.send(datagramPacketForSending);
                            }
                            //System.out.println("Ответ " + commandName + " отправлен клиенту " + clientAddress + ":" + clientPort);
                        }
                        case "exit" -> System.exit(0);
                    }
                }
            }
        }
    }
}