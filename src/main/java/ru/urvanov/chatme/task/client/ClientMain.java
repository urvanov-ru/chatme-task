package ru.urvanov.chatme.task.client;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ru.urvanov.chatme.task.PropertiesLoadException;

public class ClientMain {

    public static void main(String[] args) throws PropertiesLoadException {
        ClientProperties clientProperties = new ClientProperties();
        ExecutorService executorService = Executors.newFixedThreadPool(clientProperties.getThreadsCount());
        List<Future<ClientResponse>> responses = new ArrayList<>();
        for (int n = 0; n < 10_000; n++) {
            ClientGetCallable taskGet = new ClientGetCallable(clientProperties);
            responses.add(executorService.submit(taskGet));
            ClientPostCallable taskPost = new ClientPostCallable(clientProperties);
            responses.add(executorService.submit(taskPost));
        }
        int successResults = 0;
        while (!responses.isEmpty()) {
            ListIterator<Future<ClientResponse>> listIterator = responses
                    .listIterator();
            while (listIterator.hasNext()) {
                Future<ClientResponse> future = listIterator.next();
                if (!future.isDone()) continue;
                try {
                    ClientResponse response = future.get();
                    if (!response.isSuccess()) {
                        System.out.println("A request failed.");
                    } else {
                        successResults++;
                        System.out.println("Success results: " + successResults);
                    }
                    listIterator.remove();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        executorService.shutdown();
    }
}
