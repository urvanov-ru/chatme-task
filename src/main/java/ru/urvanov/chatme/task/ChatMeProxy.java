package ru.urvanov.chatme.task;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URI;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.Callable;

import ru.urvanov.chatme.task.cache.InMemoryCache;
import ru.urvanov.chatme.task.http.HttpItem;
import ru.urvanov.chatme.task.http.HttpItemParser;

/**
 * @author fedor
 *
 */
public class ChatMeProxy implements Callable<ChatMeProxyResponse> {

    private static final String CONTENT_LENGTH = "CONTENT-LENGTH";

    private Socket clientSocket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private byte[] requestBytes;
    private String host;
    private int port;
    private String method;
    private String uri;
    private InMemoryCache inMemoryCache;

    public ChatMeProxy(Socket clientSocket, InMemoryCache inMemoryCache) {
        this.clientSocket = clientSocket;
        this.inMemoryCache = inMemoryCache;
    }
    
    public ChatMeProxy(ChatMeProxyResponse chatMeProxyResponse, InMemoryCache inMemoryCache) {
        this.clientSocket = chatMeProxyResponse.getClientSocket();
        this.requestBytes = chatMeProxyResponse.getRequestBytes();
        this.host = chatMeProxyResponse.getHost();
        this.port = chatMeProxyResponse.getPort();
        this.inMemoryCache = inMemoryCache;
        this.method = chatMeProxyResponse.getMethod();
        this.uri = chatMeProxyResponse.getUri();
        this.inputStream = chatMeProxyResponse.getInputStream();
        this.outputStream = chatMeProxyResponse.getOutputStream();
    }

    @Override
    public ChatMeProxyResponse call() throws Exception {
        if (requestBytes == null) {
            
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    ) {
                inputStream = new BufferedInputStream(clientSocket.getInputStream());
                outputStream = clientSocket.getOutputStream();
                
                HttpItemParser parser = new HttpItemParser(inputStream);
                HttpItem httpItem = parser.parseRequest();
                this.host = httpItem.getHost();
                this.port = httpItem.getPort();
                this.requestBytes = httpItem.getRequestBytes();
                this.method = httpItem.getMethod();
                this.uri = httpItem.getUri();
                if ("GET".equals(method)) {
                    byte[] result = inMemoryCache.get(httpItem.getUri());
                    if (result != null) {
                        outputStream.write(result);
                        clientSocket.close();
                        return null;
                    }
                }
            }
        }
        byte[] response = null;
        try {
            
            try (Socket socket = new Socket(host, port);
                    OutputStream backendOutputStream = socket.getOutputStream();
                    InputStream backendInputStream = socket.getInputStream()) {
                backendOutputStream.write(requestBytes);
                    HttpItemParser parser = new HttpItemParser(backendInputStream);
                    HttpItem item = parser.parseResponse();
                    response = item.getRequestBytes();
                    inMemoryCache.add(this.uri, response);
            }
        } catch (IOException ioex) {
            ioex.printStackTrace();
            ChatMeProxyResponse result = new ChatMeProxyResponse();
            result.setHost(host);
            result.setPort(port);
            result.setMethod(method);
            result.setRequestBytes(requestBytes);
            result.setRetry(true);
            result.setInputStream(inputStream);
            result.setOutputStream(outputStream);
            result.setUri(this.uri);
            return result;
        }
        outputStream.write(response);
        
        clientSocket.close();
        return null;
    }

}
