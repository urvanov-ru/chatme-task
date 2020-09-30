package ru.urvanov.chatme.task.http;

public class HttpItem {

    private int contentLength;
    private String method;
    private String uri;
    private String host;
    private int port;
    private byte[] requestBytes;
    public int getContentLength() {
        return contentLength;
    }
    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
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
    public byte[] getRequestBytes() {
        return requestBytes;
    }
    public void setRequestBytes(byte[] requestBytes) {
        this.requestBytes = requestBytes;
    }
    
    

}
