package ru.urvanov.chatme.task.http;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class HttpItemParser {
    
    private static final String CONTENT_LENGTH = "CONTENT-LENGTH";

    private InputStream inputStream;
    
    private enum ParseType {
        REQUEST,
        RESPONSE
    }
    
    public HttpItemParser(InputStream inputStream) {
        this.inputStream = new BufferedInputStream(inputStream);
    }
    
    public HttpItem parseRequest() throws IOException, URISyntaxException {
        return this.parseInner(ParseType.REQUEST);
    }
    
    public HttpItem parseResponse() throws IOException, URISyntaxException {
        return this.parseInner(ParseType.RESPONSE);
    }
    
    
    public HttpItem parseInner(ParseType parseType) throws IOException, URISyntaxException {

        HttpItem result = new HttpItem();
        
        // CR -  1
        // CRLF -2
        // CRLFCR - 3
        // CRLFCRLF = 4
        int headersEndCounter = 0;
        
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ) {
            while (headersEndCounter != 4) {
                int readedByte = inputStream.read();
                byteArrayOutputStream.write(readedByte);
                if ((readedByte == '\r') && ((headersEndCounter == 0) || (headersEndCounter == 2) )) {
                    headersEndCounter++;
                } else if ((readedByte == '\n') && ((headersEndCounter == 1) || (headersEndCounter == 3))) {
                    headersEndCounter++;
                } else {
                    headersEndCounter = 0;
                }
            }
            // We read headers.
            
            byte[] headersRequestBytes = byteArrayOutputStream.toByteArray();
            
            
            String headersLine = new String(headersRequestBytes, StandardCharsets.US_ASCII);
            String[] headers = headersLine.split("\r\n");
            String firstLine = headers[0];
            // We support only HTTP 1.1
            String[] firstLineArray = firstLine.split("\s");
            if (firstLineArray.length != 3) {
                throw new IllegalStateException("Unsupported HTTP request.");
            }
            switch (parseType) {
            case REQUEST:
                result.setMethod(firstLineArray[0]);
                String uriString = firstLineArray[1];  // It is absolute because we are proxy
                                                 // https://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html
                result.setUri(uriString);
                URI uri = new URI(uriString);
                // This works only for proxies. 
                // For all other choices we must parse Host header.
                result.setHost(uri.getHost());
                result.setPort(uri.getPort());
                String httpVersion = firstLineArray[2];
                if (!httpVersion.equals("HTTP/1.1")) {
                    throw new IllegalStateException(
                            "Unsupported HTTP version " + httpVersion);
                }
                break;
            case RESPONSE:
                // do nothing for now.
                break;
            }
            String[][] headersKeyValue = new String[headers.length - 1][];
            for (int n = 1; n < headers.length; n++) {
                headersKeyValue[n - 1] = headers[n].split(":");
                headersKeyValue[n - 1][0] = headersKeyValue[n - 1][0].strip();
                headersKeyValue[n - 1][1] = headersKeyValue[n - 1][1].strip();
            }

            int contentLength = 0;
            for (int n = 0; n < headersKeyValue.length; n++) {
                if (CONTENT_LENGTH.equals(headersKeyValue[n][0].toUpperCase(Locale.ROOT))) {
                    contentLength = Integer.parseInt(headersKeyValue[n][1]);
                }
            }
            if (contentLength > 0) {
                byte[] bodyBytes = inputStream.readNBytes(contentLength);
                byteArrayOutputStream.write(bodyBytes);
            }
            result.setRequestBytes(byteArrayOutputStream.toByteArray());
        }
        return result;
    }

}
