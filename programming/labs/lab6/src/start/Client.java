/**
 *@author Anton Panochko
 **/
package start;

import client.ClientManager;

import java.io.IOException;
public class Client {
    public static void main(String[] args) throws IOException, ClassNotFoundException {ClientManager.makeRequest();}
}