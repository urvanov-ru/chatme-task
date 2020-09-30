/**
 * 
 */
package ru.urvanov.chatme.task.backend;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.urvanov.chatme.task.PropertiesLoadException;

/**
 * @author fedor
 *
 */
public class BackendMain {

    /**
     * @param args
     * @throws IOException 
     * @throws PropertiesLoadException 
     */
    public static void main(String[] args) throws IOException, PropertiesLoadException {
        BackendProperties properties = new BackendProperties();
        ExecutorService executorService = Executors.newFixedThreadPool(properties.getThreadsCount());
        ServerSocket serverSocket = new ServerSocket(
                properties.getBackendPort());
        while (true) {
            Socket clientSocket = serverSocket.accept();
            executorService.submit(new BackendHandler(clientSocket));
        }
    }

}
