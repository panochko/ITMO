/**
 *@author Anton Panochko
 **/
package start;

import client.ClientManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Client {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        List<String> hosts;
        if (args.length == 0)
            hosts = List.of("localhost:60000");
        else
            hosts = Arrays.asList(args);
        ClientManager.makeRequest(hosts);
    }
}