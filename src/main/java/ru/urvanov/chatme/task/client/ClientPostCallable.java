package ru.urvanov.chatme.task.client;

import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.concurrent.Callable;

public class ClientPostCallable implements Callable<ClientResponse> {

    private ClientProperties clientProperties;
    
    public ClientPostCallable(ClientProperties clientProperties) {
        this.clientProperties = clientProperties;
    }

    @Override
    public ClientResponse call() throws Exception {
        ClientResponse result = new ClientResponse();
        HttpRequest request = HttpRequest.newBuilder().uri(new URI(clientProperties.getUrl())).POST(BodyPublishers.ofString("{ \"myfield1\" : 100}")).build();
        HttpClient client = HttpClient.newBuilder().proxy(
                ProxySelector.of(InetSocketAddress.createUnresolved(clientProperties.getProxyHost(), clientProperties.getProxyPort()))).build();
        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            result.setSuccess(true);
        }
        return result;
    }


}
