/**
 * Вводные: есть клиенты и бекэнд, взаимодействие происходит по rest.
 * Возникающие нештатные ситуации: бекенд оказывается недоступным на N секунд, за это время клиенты отправляют 100-1000 сообщений системе.
 * Задача: реализовать кеширующую прокси с очередью, обеспечить гарантирующее доставку всех запросов на бекенд после его восстановления.
 * Использовать только java без сторонних библиотек.
 * Приложение должно быть многопоточным с функциями кеширования.
 */
package ru.urvanov.chatme.task;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import ru.urvanov.chatme.task.cache.InMemoryCache;

/**
 * @author fedor
 *
 */
public class ChatMeMain {

    /**
     * @param args
     * @throws IOException
     * @throws PropertiesLoadException
     */
    public static void main(String[] args)
            throws IOException, PropertiesLoadException {
        ServerProperties properties = new ServerProperties();

        InMemoryCache inMemoryCache = new InMemoryCache(properties.getCacheMillis());
        
        ServerSocket serverSocket = new ServerSocket(
                properties.getPortNumber());
        serverSocket.setSoTimeout(properties.getSoTimeout());
        ScheduledExecutorService executorService = Executors
                .newScheduledThreadPool(properties.getThreadsCount());
        List<Future<ChatMeProxyResponse>> futures = new ArrayList<>();
        int requestsProcessed = 0;
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                futures.add(executorService.submit(new ChatMeProxy(clientSocket, inMemoryCache)));
            } catch (SocketTimeoutException ste) {
                // System.out.println("ServerSocket accept timeout.");
                // It is ok.
            } catch (IOException ioex) {
                ioex.printStackTrace();
            }

            ListIterator<Future<ChatMeProxyResponse>> listIterator = futures.listIterator();
            while (listIterator.hasNext()) {
                Future<ChatMeProxyResponse> future = listIterator.next();
                if (future.isDone()) {
                    try {
                        ChatMeProxyResponse result = future.get();
                        if ((result != null) && (result.isRetry())) {
                            listIterator.remove();
                            listIterator.add(executorService.schedule(
                                    new ChatMeProxy(result, inMemoryCache),
                                    properties.getBackendUnavailableWaitMillis(),
                                    TimeUnit.MILLISECONDS));
                        } else {
                            requestsProcessed++;
                            System.out.println("requestsProcessed: " + requestsProcessed);
                            listIterator.remove();
                        }
                    } catch (ExecutionException | InterruptedException | CancellationException e) {
                        e.printStackTrace();
                        listIterator.remove();
                    }
                }
            }
        }
        // We never stop the application
        //inMemoryCache.stop();
    }

}
