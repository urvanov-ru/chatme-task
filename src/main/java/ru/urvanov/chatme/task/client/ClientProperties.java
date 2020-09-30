package ru.urvanov.chatme.task.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import ru.urvanov.chatme.task.PropertiesLoadException;

public class ClientProperties {
    
    private static final String FILENAME = "client.properties";
    
    private static final String PROXY_HOST = "client.proxy.host";
    
    private static final String PROXY_PORT = "client.proxy.port";
    
    private static final String URL = "client.url";
    
    private static final String THREADS_COUNT = "client.threads.count";
    
    private String proxyHost;
    private int proxyPort;
    private String url;
    private int threadsCount;
    
    public ClientProperties() throws PropertiesLoadException {
        Properties properties = new Properties();
        try (InputStream propertiesInputStream = ClientProperties.class.getResourceAsStream(FILENAME)) {
            properties.load(propertiesInputStream);
        } catch (IOException e) {
            throw new PropertiesLoadException("Failed to load properties from \"" + FILENAME + "\".", e);
        }
        this.proxyHost = properties.getProperty(PROXY_HOST);
        this.proxyPort = Integer.parseInt(properties.getProperty(PROXY_PORT));
        this.url = properties.getProperty(URL);
        this.threadsCount = Integer.parseInt(properties.getProperty(THREADS_COUNT));
    }

    public String getProxyHost() {
        return this.proxyHost;
    }

    public int getProxyPort() {
        return this.proxyPort;
    }

    public String getUrl() {
        return this.url;
    }

    public int getThreadsCount() {
        return this.threadsCount;
    }
}
