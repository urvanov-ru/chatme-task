package ru.urvanov.chatme.task.backend;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import ru.urvanov.chatme.task.http.HttpItem;
import ru.urvanov.chatme.task.http.HttpItemParser;

public class BackendHandler implements Runnable {

    private static final String CONTENT_LENGTH = "CONTENT-LENGTH";
    
    private static final String DEFAULT_RESPONSE = "HTTP/1.1 200 OK\r\n"
            + "Date: Mon, 27 Jul 2009 12:28:53 GMT\r\n"
            + "Server: Apache/2.2.14 (Win32)\r\n"
            + "Last-Modified: Wed, 22 Jul 2009 19:15:56 GMT\r\n"
            + "Content-Length: 19\r\n"
            + "Content-Type: application/json\r\n"
            + "Connection: Closed\r\n\r\n"
            + "{ \"success\": true }";
    
    
    private InputStream inputStream;
    private OutputStream outputStream;
    private Socket clientSocket;
    
    public BackendHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
 
    @Override
    public void run() {
        
        try {
            inputStream = clientSocket.getInputStream();
            outputStream = clientSocket.getOutputStream();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        
        HttpItemParser httpItemParser = new HttpItemParser(inputStream);
        try {
            // just read all http request data from the input
            HttpItem httpItem = httpItemParser.parseRequest();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
        
        try {
            outputStream.write(DEFAULT_RESPONSE.getBytes(StandardCharsets.US_ASCII));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Default response has been wrote");
    }

}
