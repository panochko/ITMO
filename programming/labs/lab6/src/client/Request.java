package client;

import commands.AbstractCommand;
import design.Color;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.List;

public class Request {
    public static void request(Container<? extends AbstractCommand> container) throws IOException {
        if (container == null)
            throw new IllegalArgumentException(Color.RED + "контейнер не может быть null");
        InetAddress inetAddress = InetAddress.getByName("localhost");
        int port = 60000;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(container);
        byte[] data = byteArrayOutputStream.toByteArray();
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, inetAddress, port);
        try (DatagramSocket datagramSocket = new DatagramSocket()) {
            datagramSocket.setSoTimeout(100);
            datagramSocket.send(sendPacket);
            if (container.getCommand().getCommandName().equals("exit")) return;
            System.out.println(Color.RESET + "Запрос отправлен, ожидаем ответ...");
            byte[] receiveBuff = new byte[60000];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuff, receiveBuff.length);
            while (true) {
                try {
                    datagramSocket.receive(receivePacket);
                    System.out.println(Color.RESET + "Ответ получен, размер: " + receivePacket.getLength());
                    try (ByteArrayInputStream bais = new ByteArrayInputStream(receivePacket.getData(), 0, receivePacket.getLength());
                         ObjectInputStream ois = new ObjectInputStream(bais)) {
                        Object response = ois.readObject();
                        if (response instanceof List<?> list) {
                            if (list.isEmpty())
                                System.out.println(Color.GREY + "пустой ответ от сервера");
                            else {
                                for (Object line : list)
                                    System.out.println(Color.BLUE + line);
                            }
                        } else
                            System.out.println(response);
                    } catch (ClassNotFoundException e) {
                        System.err.println(Color.RED + "ошибка десериализации ответа: " + e.getMessage());
                    }
                } catch (SocketTimeoutException e) {
                    break;
                }
            }
        }
    }
}