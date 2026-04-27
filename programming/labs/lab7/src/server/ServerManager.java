package server;

import SEclasses.*;
import client.Container;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import commands.*;
import design.Color;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerManager {
    private static final Gson gson = new Gson();
    private final static ByteBuffer buffer = ByteBuffer.allocate(10000);
    public static final Logger logger = LoggerFactory.getLogger(ServerManager.class);
    private static final String URL = "jdbc:postgresql://localhost:5432/studs";
    // ssh -L 5432:pg:5432 -p 2222 s505218@helios.se.ifmo.ru
    private static final ReadWriteLock RW_LOCK = new ReentrantReadWriteLock();
    private static final Lock READ_LOCK = RW_LOCK.readLock();
    private static final Lock WRITE_LOCK = RW_LOCK.writeLock();
    private static final ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
    private static final ExecutorService readingPool = Executors.newCachedThreadPool();
    private static final ExecutorService processingPool = Executors.newCachedThreadPool();
    private static final ExecutorService sendingPool = Executors.newCachedThreadPool();
    public ConcurrentHashMap<String, String> getMap() {return ServerManager.map;}
    public static void response(String nameOfCollection, String dbUser, String dbPass) throws IOException, ClassNotFoundException, SQLException {
        InetAddress inetAddress = InetAddress.getByName("localhost");
        int port = 60000;
        logger.info("Server started on {}:{}", inetAddress, port);

        switch (nameOfCollection) {
            case "TreeSet" -> AbstractCommand.setCollection(new TreeSet<>());
            case "ArrayList" -> AbstractCommand.setCollection(new ArrayList<>());
            case "PriorityQueue" -> AbstractCommand.setCollection(new PriorityQueue<>());
            default -> AbstractCommand.setMapCollection(new LinkedHashMap<>());
        }

        try (Connection conn = DriverManager.getConnection(URL, dbUser, dbPass)) {
            createTable(conn);
        }

        WRITE_LOCK.lock();
        try {
            List<Worker> dbData = loadWorkers(dbUser, dbPass);
            if (AbstractCommand.getCollection() != null)
                AbstractCommand.getCollection().addAll(dbData);
            if (AbstractCommand.getMapCollection() != null)
                dbData.forEach(worker -> AbstractCommand.getMapCollection().put(worker.getId(), worker));
            logger.info("Коллекция загружена из БД. Количество элементов: {}", dbData.size());
        } finally {
            WRITE_LOCK.unlock();
        }
        map.put(dbUser, hashWithSHA512(dbPass));
        logger.info("Loading data into {}", nameOfCollection);
        try (DatagramSocket datagramSocket = new DatagramSocket(port, inetAddress)) {
            while (true) {
                buffer.clear();
                DatagramPacket datagramPacket = new DatagramPacket(buffer.array(), buffer.array().length);
                datagramSocket.receive(datagramPacket);
                InetAddress clientAddress = datagramPacket.getAddress();
                int clientPort = datagramPacket.getPort();
                logger.info("Received a package from {}:{}", clientAddress, clientPort);
                byte[] receivedBytes = new byte[datagramPacket.getLength()];
                System.arraycopy(buffer.array(), 0, receivedBytes, 0, datagramPacket.getLength());
                readingPool.submit(() -> {
                    try (var byteArrayInputStream = new ByteArrayInputStream(receivedBytes);
                         var objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
                        Object object = objectInputStream.readObject();
                        if (!(object instanceof Container<? extends AbstractCommand> container)) {
                            String errorMessage = Color.RED + "получен объект типа " + object.getClass() + ", ожидался Container";
                            sendingPool.submit(() -> {
                                try {
                                    sendResponse(errorMessage, inetAddress, clientPort, datagramSocket);
                                } catch (IOException e) {
                                    logger.error("Error sending response", e);
                                }
                            });
                            return;
                        }
                        String commandName = container.getCommand().getCommandName();
                        logger.info("Received command: {}", commandName);
                        processingPool.submit(() -> {
                            try {
                                if (commandName.matches("filter_contains_name [A-Za-zА-Яа-я ,-.]+") ||
                                        commandName.matches("filter_starts_with_name [A-Za-zА-Яа-я ,-.]+")) {
                                    READ_LOCK.lock();
                                    try {
                                        List<Worker> filtered = new ArrayList<>(container.getCommand().executeWithArgsAndCollection(
                                                container.getList().getFirst(),
                                                AbstractCommand.getCollection(),
                                                AbstractCommand.getMapCollection()));
                                        sendResponse(filtered, inetAddress, clientPort, datagramSocket);
                                    } finally {
                                        READ_LOCK.unlock();
                                    }
                                }
                                else if (commandName.matches("remove_by_id [0-9]+")) {
                                    Integer id = container.getIntegerList().getFirst();
                                    String owner = container.getUserArray()[0];
                                    String password = container.getUserArray()[1];
                                    String sql = "DELETE FROM applicationTable WHERE id = ? AND owner = ?";
                                    try (var conn = DriverManager.getConnection(URL, owner, password);
                                         var ps = conn.prepareStatement(sql)) {
                                        ps.setInt(1, id);
                                        ps.setString(2, owner);
                                        int deleted = ps.executeUpdate();
                                        if (deleted > 0) {
                                            WRITE_LOCK.lock();
                                            try {
                                                refreshCollectionFromDB(owner, password);
                                            } finally {
                                                WRITE_LOCK.unlock();
                                            }
                                            sendResponse(Color.BLUE + "работник с id " + id + " удалён", inetAddress, clientPort, datagramSocket);
                                        } else {
                                            sendResponse(Color.BLUE + "работника с id " + id + " не существует или он вам не принадлежит", inetAddress, clientPort, datagramSocket);
                                        }
                                    } catch (SQLException e) {
                                        sendResponse(Color.RED + "ошибка PostgreSQL: " + e.getMessage(), inetAddress, clientPort, datagramSocket);
                                    }
                                }
                                else if (commandName.matches("update [0-9]+")) {
                                    Integer id = container.getIntegerList().getFirst();
                                    Worker newWorker = container.getWorker();
                                    if (newWorker == null) {
                                        sendResponse(Color.GREY + "не переданы данные для обновления", inetAddress, clientPort, datagramSocket);
                                        return;
                                    }
                                    String owner = container.getUserArray()[0];
                                    String password = container.getUserArray()[1];
                                    String sql = """
                                        UPDATE applicationTable
                                        SET name = ?, coordinates = ?::jsonb, salary = ?, position = ?, status = ?, person = ?::jsonb
                                        WHERE id = ? AND owner = ?
                                    """;
                                    try (var conn = DriverManager.getConnection(URL, owner, password);
                                         var ps = conn.prepareStatement(sql)) {
                                        ps.setString(1, newWorker.getName());
                                        ps.setString(2, convertToJson(newWorker.getCoordinates()));
                                        ps.setInt(3, newWorker.getSalary());
                                        ps.setString(4, newWorker.getPosition().toString());
                                        ps.setString(5, newWorker.getStatus().toString());
                                        ps.setString(6, convertToJson(newWorker.getPerson()));
                                        ps.setInt(7, id);
                                        ps.setString(8, owner);
                                        int updated = ps.executeUpdate();
                                        if (updated > 0) {
                                            WRITE_LOCK.lock();
                                            try {
                                                refreshCollectionFromDB(owner, password);
                                            } finally {
                                                WRITE_LOCK.unlock();
                                            }
                                            sendResponse(Color.BLUE + "работник с id " + id + " обновлён", inetAddress, clientPort, datagramSocket);
                                        } else {
                                            sendResponse(Color.BLUE + "работник с id " + id + " не найден или не принадлежит вам", inetAddress, clientPort, datagramSocket);
                                        }
                                    } catch (SQLException e) {
                                        sendResponse(Color.RED + "ошибка PostgreSQL: " + e.getMessage(), inetAddress, clientPort, datagramSocket);
                                    }
                                }
                                else if (commandName.matches("remove_greater [0-9]+")) {
                                    Integer id = container.getIntegerList().getFirst();
                                    String owner = container.getUserArray()[0];
                                    String password = container.getUserArray()[1];
                                    String sql = "DELETE FROM applicationTable WHERE id > ? AND owner = ?";
                                    try (var conn = DriverManager.getConnection(URL, owner, password);
                                         var ps = conn.prepareStatement(sql)) {
                                        ps.setInt(1, id);
                                        ps.setString(2, owner);
                                        int deleted = ps.executeUpdate();
                                        if (deleted > 0) {
                                            WRITE_LOCK.lock();
                                            try {
                                                refreshCollectionFromDB(owner, password);
                                            } finally {
                                                WRITE_LOCK.unlock();
                                            }
                                        }
                                        sendResponse("удалено работников: " + deleted, inetAddress, clientPort, datagramSocket);
                                    } catch (SQLException e) {
                                        sendResponse(Color.RED + "ошибка PostgreSQL: " + e.getMessage(), inetAddress, clientPort, datagramSocket);
                                    }
                                }
                                else if (commandName.matches("execute_script( [A-Za-zА-Яа-я0-9_.]*)?")) {
                                    ExecuteScript executeScript = (ExecuteScript) container.getCommand();
                                    List<String> responseList = executeScriptCommands(executeScript, container.getList(), container.getUserArray());
                                    sendResponse(responseList, inetAddress, clientPort, datagramSocket);
                                }
                                else switch (commandName) {
                                        case "add" -> {
                                            try {
                                                String response = handleAdd(container, AbstractCommand.getCollection(), AbstractCommand.getMapCollection());
                                                sendResponse(response, inetAddress, clientPort, datagramSocket);
                                            } catch (SQLException e) {
                                                sendResponse(Color.RED + "ошибка PostgreSQL: " + e.getMessage(), inetAddress, clientPort, datagramSocket);
                                            }
                                        }
                                        case "add_if_max" -> {
                                            try {
                                                String response = handleAddIfMax(container, AbstractCommand.getCollection(), AbstractCommand.getMapCollection());
                                                sendResponse(response, inetAddress, clientPort, datagramSocket);
                                            } catch (SQLException e) {
                                                sendResponse(Color.RED + "ошибка PostgreSQL: " + e.getMessage(), inetAddress, clientPort, datagramSocket);
                                            }
                                        }
                                        case "add_if_min" -> {
                                            try {
                                                String response = handleAddIfMin(container, AbstractCommand.getCollection(), AbstractCommand.getMapCollection());
                                                sendResponse(response, inetAddress, clientPort, datagramSocket);
                                            } catch (SQLException e) {
                                                sendResponse(Color.RED + "ошибка PostgreSQL: " + e.getMessage(), inetAddress, clientPort, datagramSocket);
                                            }
                                        }
                                        case "clear" -> {
                                            String owner = container.getUserArray()[0];
                                            String password = container.getUserArray()[1];
                                            String sql = "DELETE FROM applicationTable WHERE owner = ?";
                                            try (var conn = DriverManager.getConnection(URL, owner, password);
                                                 var ps = conn.prepareStatement(sql)) {
                                                ps.setString(1, owner);
                                                ps.executeUpdate();
                                                WRITE_LOCK.lock();
                                                try {
                                                    refreshCollectionFromDB(owner, password);
                                                } finally {
                                                    WRITE_LOCK.unlock();
                                                }
                                                sendResponse(Color.BLUE + "коллекция очищена (ваши элементы)", inetAddress, clientPort, datagramSocket);
                                            } catch (SQLException e) {
                                                sendResponse(Color.RED + "ошибка PostgreSQL: " + e.getMessage(), inetAddress, clientPort, datagramSocket);
                                            }
                                        }
                                        case "help" -> {
                                            Help help = (Help) container.getCommand();
                                            List<String> helpList = help.executeWithoutArgs();
                                            sendResponse(helpList, inetAddress, clientPort, datagramSocket);
                                        }
                                        case "min_by_coordinates" -> {
                                            String sql = """
                                            WITH RankedWorkers AS(
                                                SELECT *, RANK() OVER(
                                                    ORDER BY
                                                        (coordinates->>'x')::double precision ASC,
                                                        (coordinates->>'y')::double precision ASC,
                                                        (coordinates->>'z')::double precision ASC
                                                ) as rnk
                                                FROM applicationTable
                                            )
                                            SELECT * FROM RankedWorkers WHERE rnk = 1;
                                        """;
                                            List<Worker> list = new ArrayList<>();
                                            try (var conn = DriverManager.getConnection(URL, container.getUserArray()[0], container.getUserArray()[1]);
                                                 var ps = conn.prepareStatement(sql)) {
                                                var resultSet = ps.executeQuery();
                                                while (resultSet.next()) {
                                                    list.add(mapResultSetToWorker(resultSet));
                                                }
                                                sendResponse(list, inetAddress, clientPort, datagramSocket);
                                            }
                                        }
                                        case "info" -> {
                                            String sql = "SELECT COUNT(*) FROM applicationTable";
                                            int number = 0;
                                            Info info = (Info) container.getCommand();
                                            try (var conn = DriverManager.getConnection(URL, container.getUserArray()[0], container.getUserArray()[1]);
                                                 var ps = conn.prepareStatement(sql)) {
                                                var resultSet = ps.executeQuery();
                                                if (resultSet.next()) number = resultSet.getInt(1);
                                            }
                                            List<String> list = info.executeWithIntAndCollection(number, AbstractCommand.getCollection(), AbstractCommand.getMapCollection());
                                            sendResponse(list, inetAddress, clientPort, datagramSocket);
                                        }
                                        case "show" -> {
                                            String sql = "SELECT * FROM applicationTable";
                                            List<Worker> list = new ArrayList<>();
                                            try (var conn = DriverManager.getConnection(URL, container.getUserArray()[0], container.getUserArray()[1]);
                                                 var ps = conn.prepareStatement(sql)) {
                                                var resultSet = ps.executeQuery();
                                                while (resultSet.next()) {
                                                    list.add(mapResultSetToWorker(resultSet));
                                                }
                                                sendResponse(list, inetAddress, clientPort, datagramSocket);
                                            }
                                        }
                                    }
                            } catch (Exception e) {
                                logger.error("Error processing command", e);
                                try {
                                    sendResponse(Color.RED + "ошибка обработки: " + e.getMessage(), inetAddress, clientPort, datagramSocket);
                                } catch (IOException ex) {
                                    logger.error("Error sending error response", ex);
                                }
                            }
                        });
                    } catch (Exception e) {
                        logger.error("Error reading request", e);
                    }
                });
            }
        }
    }

    private static void createTable(Connection connection) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS applicationTable(
                id SERIAL PRIMARY KEY,
                name TEXT NOT NULL,
                coordinates JSONB NOT NULL,
                creationDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                salary INTEGER NOT NULL,
                startDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                position TEXT NOT NULL,
                status TEXT NOT NULL,
                person JSONB NOT NULL,
                owner TEXT NOT NULL
            );
        """;
        try (var stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static void refreshCollectionFromDB(String username, String password) throws SQLException {
        List<Worker> dbData = loadWorkers(username, password);
        if (AbstractCommand.getCollection() != null) {
            AbstractCommand.getCollection().clear();
            AbstractCommand.getCollection().addAll(dbData);
        }
        if (AbstractCommand.getMapCollection() != null) {
            AbstractCommand.getMapCollection().clear();
            dbData.forEach(w -> AbstractCommand.getMapCollection().put(w.getId(), w));
        }
    }

    private static String handleAdd(Container<? extends AbstractCommand> container, Collection<Worker> collection, Map<Integer, Worker> mapCollection) throws SQLException {
        Worker worker = container.getWorker();
        if (worker == null) return Color.GREY + "пустые данные работника";
        String[] userArray = container.getUserArray();
        Worker saved = saveWorkerToDB(worker, userArray[0], userArray[1]);
        WRITE_LOCK.lock();
        try {
            if (mapCollection == null) collection.add(saved);
            if (collection == null) mapCollection.put(saved.getId(), saved);
        } finally {
            WRITE_LOCK.unlock();
        }
        return Color.BLUE + "работник с id " + saved.getId() + " добавлен в коллекцию";
    }

    private static String handleAddIfMax(Container<?> container, Collection<Worker> collection, Map<Integer, Worker> mapCollection) throws SQLException {
        Worker worker = container.getWorker();
        if (worker == null) return Color.GREY + "пустые данные работника";
        String[] userArray = container.getUserArray();
        Worker saved = saveMaxWorkerToDB(worker, userArray[0], userArray[1]);
        if (saved == null) return Color.GREY + "работник с salary " + worker.getSalary() + " не добавлен (не больше максимума)";
        WRITE_LOCK.lock();
        try {
            if (mapCollection == null) collection.add(saved);
            if (collection == null) mapCollection.put(saved.getId(), saved);
        } finally {
            WRITE_LOCK.unlock();
        }
        return Color.BLUE + "работник с id " + saved.getId() + " добавлен в коллекцию";
    }

    private static String handleAddIfMin(Container<?> container, Collection<Worker> collection, Map<Integer, Worker> mapCollection) throws SQLException {
        Worker worker = container.getWorker();
        if (worker == null) return Color.GREY + "пустые данные работника";
        String[] userArray = container.getUserArray();
        Worker saved = saveMinWorkerToDB(worker, userArray[0], userArray[1]);
        if (saved == null) return Color.GREY + "работник с salary " + worker.getSalary() + " не добавлен (не меньше минимума)";
        WRITE_LOCK.lock();
        try {
            if (mapCollection == null) collection.add(saved);
            if (collection == null) mapCollection.put(saved.getId(), saved);
        } finally {
            WRITE_LOCK.unlock();
        }
        return Color.BLUE + "работник с id " + saved.getId() + " добавлен в коллекцию";
    }

    private static Worker saveWorkerToDB(Worker worker, String username, String password) throws SQLException {
        String sql = """
            INSERT INTO applicationTable
            (name, coordinates, salary, position, status, person, owner)
            VALUES (?, ?::jsonb, ?, ?, ?, ?::jsonb, ?)
            RETURNING id
        """;
        int generatedId;
        try (var conn = DriverManager.getConnection(URL, username, password);
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, worker.getName());
            ps.setString(2, convertToJson(worker.getCoordinates()));
            ps.setInt(3, worker.getSalary());
            ps.setString(4, worker.getPosition().toString());
            ps.setString(5, worker.getStatus().toString());
            ps.setString(6, convertToJson(worker.getPerson()));
            ps.setString(7, username);
            try (var rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("Не удалось получить сгенерированный id");
                generatedId = rs.getInt("id");
            }
        }
        String selectSql = "SELECT * FROM applicationTable WHERE id = ?";
        try (var conn = DriverManager.getConnection(URL, username, password);
             var ps = conn.prepareStatement(selectSql)) {
            ps.setInt(1, generatedId);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToWorker(rs);
                throw new SQLException("Работник с id " + generatedId + " не найден после вставки");
            }
        }
    }

    private static Worker saveMaxWorkerToDB(Worker worker, String username, String password) throws SQLException {
        int maxSalary = 0;
        String sqlMax = "SELECT COALESCE(MAX(salary), 0) FROM applicationTable";
        try (var conn = DriverManager.getConnection(URL, username, password);
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sqlMax)) {
            if (rs.next()) maxSalary = rs.getInt(1);
        }
        if (worker.getSalary() <= maxSalary) return null;
        return saveWorkerToDB(worker, username, password);
    }

    private static Worker saveMinWorkerToDB(Worker worker, String username, String password) throws SQLException {
        int minSalary = Integer.MAX_VALUE;
        String sqlMin = "SELECT COALESCE(MIN(salary), 0) FROM applicationTable";
        try (var conn = DriverManager.getConnection(URL, username, password);
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sqlMin)) {
            if (rs.next()) minSalary = rs.getInt(1);
        }
        if (worker.getSalary() >= minSalary && minSalary != 0) return null;
        return saveWorkerToDB(worker, username, password);
    }

    private static List<Worker> loadWorkers(String username, String password) throws SQLException {
        List<Worker> workers = new ArrayList<>();
        String sql = "SELECT * FROM applicationTable ORDER BY id";
        try (var conn = DriverManager.getConnection(URL, username, password);
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            while (rs.next()) workers.add(mapResultSetToWorker(rs));
        }
        return workers;
    }

    private static Worker mapResultSetToWorker(ResultSet rs) throws SQLException {
        Worker w = new Worker();
        w.DBSetId(rs.getInt("id"));
        w.setName(rs.getString("name"));
        w.setCoordinates(parseCoordinatesFromJson(rs.getString("coordinates")));
        w.setSalary(rs.getInt("salary"));
        w.setPosition(Position.valueOf(rs.getString("position")));
        w.setStatus(Status.valueOf(rs.getString("status")));
        w.setPerson(parsePersonFromJson(rs.getString("person")));
        return w;
    }

    private static Coordinates parseCoordinatesFromJson(String json) throws SQLException {
        try {
            return gson.fromJson(json, Coordinates.class);
        } catch (JsonSyntaxException e) {
            throw new SQLException("Ошибка парсинга JSON координат: " + json, e);
        }
    }

    private static Person parsePersonFromJson(String json) throws SQLException {
        try {
            return gson.fromJson(json, Person.class);
        } catch (JsonSyntaxException e) {
            throw new SQLException("Ошибка парсинга JSON person: " + json, e);
        }
    }

    private static String convertToJson(Object obj) {
        return gson.toJson(obj);
    }

    private static void sendResponse(Object obj, InetAddress inetAddress, int port, DatagramSocket socket) throws IOException {
        var byteArrayOutputStream = new ByteArrayOutputStream();
        try (var objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(obj);
        }
        byte[] data = byteArrayOutputStream.toByteArray();
        socket.send(new DatagramPacket(data, data.length, inetAddress, port));
    }

    private static List<String> executeScriptCommands(ExecuteScript executor, List<String> commands, String[] userArray) throws SQLException {
        List<String> responses = new ArrayList<>();
        for (String cmd : commands) {
            if (cmd.matches("add [0-9A-Za-z-,]+")) {
                Worker w = executor.parseAdd(cmd, AbstractCommand.getCollection(), AbstractCommand.getMapCollection());
                responses.add(scriptAdd(cmd, w, userArray[0], userArray[1]));
            } else if (cmd.matches("filter_contains_name [A-Za-zА-Яа-я ,-.]+")) {
                String[] parts = cmd.split(" ", 2);
                READ_LOCK.lock();
                try {
                    responses.addAll(scriptFilterContainsName(parts[1], userArray[0], userArray[1]));
                } finally {
                    READ_LOCK.unlock();
                }
            } else if (cmd.matches("filter_starts_with_name [A-Za-zА-Яа-я ,-.]+")) {
                String[] parts = cmd.split(" ", 2);
                READ_LOCK.lock();
                try {
                    responses.addAll(scriptFilterStartsWithName(parts[1], userArray[0], userArray[1]));
                } finally {
                    READ_LOCK.unlock();
                }
            } else if (cmd.matches("remove_by_id [0-9]+")) {
                String[] parts = cmd.split(" ");
                responses.add(scriptRemoveById(Integer.parseInt(parts[1]), userArray[0], userArray[1]));
            } else if (cmd.matches("remove_greater [0-9]+")) {
                String[] parts = cmd.split(" ");
                responses.add(scriptRemoveGreater(Integer.parseInt(parts[1]), userArray[0], userArray[1]));
            } else if (cmd.matches("add_if_max [0-9A-Za-z-,]+")) {
                try {
                    Worker w = executor.parseAddIfMax(cmd, AbstractCommand.getCollection(), AbstractCommand.getMapCollection());
                    responses.add(scriptAddIfMax(cmd, w, userArray[0], userArray[1]));
                } catch (NoSuchElementException e) {
                    responses.add(Color.GREY + "команда " + cmd + " не выполнена, коллекция пуста");
                }
            } else if (cmd.matches("add_if_min [0-9A-Za-z-,]+")) {
                try {
                    Worker w = executor.parseAddIfMin(cmd, AbstractCommand.getCollection(), AbstractCommand.getMapCollection());
                    responses.add(scriptAddIfMin(cmd, w, userArray[0], userArray[1]));
                } catch (NoSuchElementException e) {
                    responses.add(Color.GREY + "команда " + cmd + " не выполнена, коллекция пуста");
                }
            } else if (cmd.equals("clear")) {
                responses.add(scriptClear(userArray[0], userArray[1]));
            } else if (cmd.equals("help")) {
                responses.addAll(scriptHelp());
            } else if (cmd.equals("min_by_coordinates")) {
                responses.addAll(scriptMinByCoordinates(userArray[0], userArray[1]));
            } else if (cmd.equals("info")) {
                responses.addAll(scriptInfo(userArray[0], userArray[1]));
            } else if (cmd.equals("show")) {
                responses.addAll(scriptShow(userArray[0], userArray[1]));
            }
        }
        return responses;
    }

    private static String scriptAdd(String cmd, Worker w, String user, String pass) throws SQLException {
        if (w == null) return Color.GREY + "команда " + cmd + " не выполнена";
        Worker saved = saveWorkerToDB(w, user, pass);
        WRITE_LOCK.lock();
        try {
            if (AbstractCommand.getCollection() != null) AbstractCommand.getCollection().add(saved);
            if (AbstractCommand.getMapCollection() != null) AbstractCommand.getMapCollection().put(saved.getId(), saved);
        } finally {
            WRITE_LOCK.unlock();
        }
        return Color.BLUE + "работник с id " + saved.getId() + " добавлен в коллекцию";
    }

    private static String scriptAddIfMax(String cmd, Worker w, String user, String pass) throws SQLException {
        if (w == null) return Color.GREY + "команда " + cmd + " не выполнена";
        Worker saved = saveMaxWorkerToDB(w, user, pass);
        if (saved == null) return Color.GREY + "работник с salary " + w.getSalary() + " не добавлен";
        WRITE_LOCK.lock();
        try {
            if (AbstractCommand.getCollection() != null) AbstractCommand.getCollection().add(saved);
            if (AbstractCommand.getMapCollection() != null) AbstractCommand.getMapCollection().put(saved.getId(), saved);
        } finally {
            WRITE_LOCK.unlock();
        }
        return Color.BLUE + "работник с id " + saved.getId() + " добавлен в коллекцию";
    }

    private static String scriptAddIfMin(String cmd, Worker w, String user, String pass) throws SQLException {
        if (w == null) return Color.GREY + "команда " + cmd + " не выполнена";
        Worker saved = saveMinWorkerToDB(w, user, pass);
        if (saved == null) return Color.GREY + "работник с salary " + w.getSalary() + " не добавлен";
        WRITE_LOCK.lock();
        try {
            if (AbstractCommand.getCollection() != null) AbstractCommand.getCollection().add(saved);
            if (AbstractCommand.getMapCollection() != null) AbstractCommand.getMapCollection().put(saved.getId(), saved);
        } finally {
            WRITE_LOCK.unlock();
        }
        return Color.BLUE + "работник с id " + saved.getId() + " добавлен в коллекцию";
    }

    private static List<String> scriptFilterContainsName(String pattern, String user, String pass) throws SQLException {
        List<String> res = new ArrayList<>();
        for (Worker w : filterWorkersByName(pattern, true, user, pass)) res.add(w.toString());
        return res;
    }

    private static List<String> scriptFilterStartsWithName(String pattern, String user, String pass) throws SQLException {
        List<String> res = new ArrayList<>();
        for (Worker w : filterWorkersByName(pattern, false, user, pass)) res.add(w.toString());
        return res;
    }

    private static String scriptRemoveById(int id, String user, String pass) throws SQLException {
        String sql = "DELETE FROM applicationTable WHERE id = ? AND owner = ?";
        try (var conn = DriverManager.getConnection(URL, user, pass);
             var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, user);
            int deleted = ps.executeUpdate();
            if (deleted > 0) {
                WRITE_LOCK.lock();
                try {
                    refreshCollectionFromDB(user, pass);
                } finally {
                    WRITE_LOCK.unlock();
                }
                return Color.BLUE + "работник с id " + id + " удалён";
            }
            return Color.BLUE + "работника с id " + id + " не существует или он вам не принадлежит";
        }
    }

    private static String scriptRemoveGreater(int id, String user, String pass) throws SQLException {
        String sql = "DELETE FROM applicationTable WHERE id > ? AND owner = ?";
        try (var conn = DriverManager.getConnection(URL, user, pass);
             var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, user);
            int deleted = ps.executeUpdate();
            if (deleted > 0) {
                WRITE_LOCK.lock();
                try {
                    refreshCollectionFromDB(user, pass);
                } finally {
                    WRITE_LOCK.unlock();
                }
            }
            return "удалено работников: " + deleted;
        }
    }

    private static String scriptClear(String user, String pass) throws SQLException {
        String sql = "DELETE FROM applicationTable WHERE owner = ?";
        try (var conn = DriverManager.getConnection(URL, user, pass);
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, user);
            ps.executeUpdate();
            WRITE_LOCK.lock();
            try {
                refreshCollectionFromDB(user, pass);
            } finally {
                WRITE_LOCK.unlock();
            }
            return Color.BLUE + "коллекция очищена (ваши элементы)";
        }
    }

    private static List<String> scriptHelp() {
        return new Help("help").executeWithoutArgs();
    }

    private static List<String> scriptMinByCoordinates(String user, String pass) throws SQLException {
        List<String> res = new ArrayList<>();
        for (Worker w : findMinByCoordinates(user, pass)) res.add(w.toString());
        return res;
    }

    private static List<String> scriptInfo(String user, String pass) throws SQLException {
        int count = countWorkersInDB(user, pass);
        return new Info("info").executeWithIntAndCollection(count, AbstractCommand.getCollection(), AbstractCommand.getMapCollection());
    }

    private static List<String> scriptShow(String user, String pass) throws SQLException {
        List<String> res = new ArrayList<>();
        for (Worker w : loadAllWorkers(user, pass)) res.add(w.toString());
        return res;
    }

    private static List<Worker> loadAllWorkers(String user, String pass) throws SQLException {
        return loadWorkers(user, pass);
    }

    private static List<Worker> findMinByCoordinates(String user, String pass) throws SQLException {
        String sql = """
            WITH RankedWorkers AS (
                SELECT *, RANK() OVER (
                    ORDER BY (coordinates->>'x')::double precision ASC,
                             (coordinates->>'y')::double precision ASC,
                             (coordinates->>'z')::double precision ASC
                ) as rnk
                FROM applicationTable
            )
            SELECT * FROM RankedWorkers WHERE rnk = 1;
        """;
        List<Worker> workers = new ArrayList<>();
        try (var conn = DriverManager.getConnection(URL, user, pass);
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            while (rs.next()) workers.add(mapResultSetToWorker(rs));
        }
        return workers;
    }

    private static int countWorkersInDB(String user, String pass) throws SQLException {
        String sql = "SELECT COUNT(*) FROM applicationTable";
        try (var conn = DriverManager.getConnection(URL, user, pass);
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    private static List<Worker> filterWorkersByName(String pattern, boolean contains, String user, String pass) throws SQLException {
        String searchPattern = contains ? "%" + pattern + "%" : pattern + "%";
        String sql = "SELECT * FROM applicationTable WHERE name LIKE ?";
        List<Worker> workers = new ArrayList<>();
        try (var conn = DriverManager.getConnection(URL, user, pass);
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, searchPattern);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) workers.add(mapResultSetToWorker(rs));
            }
        }
        return workers;
    }
    private static String hashWithSHA512(String password) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            byte[] hashedBytes = messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : hashedBytes)
                stringBuilder.append(String.format("%02x", b));
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}