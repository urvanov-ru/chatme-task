package ru.urvanov.chatme.task;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServerProperties {
    
    private static final String FILENAME = "chatme.properties";
    
    private static final String BACKEND_UNAVAILABLE_WAIT_MILLIS = "chatme.server.backendunavailablewaitmillis";
    
    private static final String THREADS_COUNT = "chatme.server.threadscount";
    
    private static final String SO_TIMEOUT = "chatme.server.sotimeout";
    
    private static final String CACHE_MILLIS = "chatme.server.cachemillis";
    
    private int portNumber;
    
    private int threadsCount;
    
    private long backendUnavailableWaitMillis;
    
    private int soTimeout;
    
    private int cacheMillis;
    
    public ServerProperties() throws PropertiesLoadException {
        Properties properties = new Properties();
        try (InputStream propertiesInputStream = ChatMeMain.class.getResourceAsStream(FILENAME)) {
            properties.load(propertiesInputStream);
        } catch (IOException e) {
            throw new PropertiesLoadException("Failed to load properties from \"" + FILENAME + "\".", e);
        }
        this.portNumber = Integer.parseInt(properties.getProperty("chatme.server.port"));
        this.threadsCount = Integer.parseInt(properties.getProperty(THREADS_COUNT));
        this.backendUnavailableWaitMillis = Long.parseLong(properties.getProperty(BACKEND_UNAVAILABLE_WAIT_MILLIS));
        this.soTimeout = Integer.parseInt(properties.getProperty(SO_TIMEOUT));
        this.cacheMillis = Integer.parseInt(properties.getProperty(CACHE_MILLIS));
    }

    public int getPortNumber() {
        return this.portNumber;
    }
    
    public int getThreadsCount() {
        return this.threadsCount;
    }
    
    public long getBackendUnavailableWaitMillis() {
        return this.backendUnavailableWaitMillis;
    }
    
    public int getSoTimeout() {
        return this.soTimeout;
    }

    public int getCacheMillis() {
        return cacheMillis;
    }

    
}
