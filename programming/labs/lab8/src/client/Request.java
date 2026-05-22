package client;

import commands.AbstractCommand;
import design.Color;

import java.io.*;
import java.net.*;
import java.util.*;

public class Request {
    public static void request(Container<? extends AbstractCommand> container, List<String> hosts) throws IOException {
        if (container == null)
            throw new IllegalArgumentException(Color.RED + "контейнер не может быть null");
        if (hosts == null || hosts.isEmpty())
            throw new IllegalArgumentException(Color.RED + "список хостов не может быть пустым");
        String cmdName = container.getCommand().getCommandName();
        boolean isMutating = !Set.of("help", "info", "show", "min_by_coordinates", "filter_contains_name", "filter_starts_with_name").contains(cmdName);
        InetAddress firstAddress = null;
        int firstPort = -1;
        byte[] data;
        Object response = null;
        boolean sentToAny = false;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(container);
        }
        data = byteArrayOutputStream.toByteArray();
        for (String hostPort : hosts) {
            String[] parts = hostPort.split(":");
            if (parts.length != 2) continue;
            String host = parts[0];
            int port;
            try {
                port = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                continue;
            }
            try (DatagramSocket socket = new DatagramSocket()) {
                socket.setSoTimeout(2000);
                InetAddress address = InetAddress.getByName(host);
                DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, port);
                socket.send(sendPacket);
                byte[] receiveBuff = new byte[60000];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuff, receiveBuff.length);
                socket.receive(receivePacket);
                try (var bais = new ByteArrayInputStream(receivePacket.getData(), 0, receivePacket.getLength());
                     var ois = new ObjectInputStream(bais)) {
                    response = ois.readObject();
                } catch (ClassNotFoundException e) {
                    System.err.println(Color.RED + "ошибка десериализации ответа: " + e.getMessage());
                }
                firstAddress = address;
                firstPort = port;
                sentToAny = true;
                break;
            } catch (IOException e) {
                //пробуем следующий сервер
            }
        }

        if (!sentToAny)
            throw new IOException(Color.RED + "ни один сервер из списка не доступен");
        if (cmdName.equals("exit")) return;
        System.out.println(Color.RESET + "запрос отправлен, ожидаем ответ...");
        System.out.println(Color.RESET + "ответ получен, размер: " + (response != null ? response.toString().length() : 0));
        if (response instanceof List<?> list) {
            if (list.isEmpty())
                System.out.println(Color.GREY + "пустой ответ от сервера");
            else {
                for (Object line : list)
                    System.out.println(Color.BLUE + line);
            }
        } else {
            System.out.println(response);
        }
        if (isMutating) {
            for (String hostPort : hosts) {
                String[] parts = hostPort.split(":");
                if (parts.length != 2) continue;
                String host = parts[0];
                int port;
                try {
                    port = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    continue;
                }
                if (host.equals(firstAddress.getHostAddress()) && port == firstPort) continue;
                try (DatagramSocket socket = new DatagramSocket()) {
                    socket.setSoTimeout(1000);
                    InetAddress address = InetAddress.getByName(host);
                    DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, port);
                    socket.send(sendPacket);
                    byte[] receiveBuff = new byte[60000];
                    DatagramPacket receivePacket = new DatagramPacket(receiveBuff, receiveBuff.length);
                    socket.receive(receivePacket);
                } catch (IOException e) {
                    System.err.println(Color.RED + "не удалось синхронизировать с сервером " + hostPort + ": " + e.getMessage());
                }
            }
        }
    }
}