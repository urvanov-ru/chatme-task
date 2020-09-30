package ru.urvanov.chatme.task;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ChatMeProxyResponse {
    private Socket clientSocket;
    private byte[] requestBytes;
    private String host;
    private int port;
    private boolean retry;
    private String method;
    private String uri;
    private InputStream inputStream;
    private OutputStream outputStream;
    
    public Socket getClientSocket() {
        return clientSocket;
    }
    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
    public byte[] getRequestBytes() {
        return requestBytes;
    }
    public void setRequestBytes(byte[] requestBytes) {
        this.requestBytes = requestBytes;
    }
    public String getHost() {
        return host;
    }
    public void setHost(String host) {
        this.host = host;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public boolean isRetry() {
        return retry;
    }
    public void setRetry(boolean retry) {
        this.retry = retry;
    }
    
    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }
    
    public String getUri() {
        return uri;
    }
    public void setUri(String uri) {
        this.uri = uri;
    }
    public InputStream getInputStream() {
        return inputStream;
    }
    public OutputStream getOutputStream() {
        return outputStream;
    }
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

}
