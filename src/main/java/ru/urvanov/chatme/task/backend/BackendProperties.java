package ru.urvanov.chatme.task.backend;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import ru.urvanov.chatme.task.PropertiesLoadException;

public class BackendProperties {
    
    private static final String FILENAME = "backend.properties";

    private static final String BACKEND_PORT="backend.port";
    
    private static final String THREADS_COUNT = "backend.threads.count";
    
    private int backendPort;
    
    private int threadsCount;
    
    public BackendProperties() throws PropertiesLoadException {
        Properties properties = new Properties();
        try (InputStream propertiesInputStream = BackendProperties.class.getResourceAsStream(FILENAME)) {
            properties.load(propertiesInputStream);
        } catch (IOException e) {
            throw new PropertiesLoadException("Failed to load properties from \"" + FILENAME + "\".", e);
        }
        this.backendPort = Integer.parseInt(properties.getProperty(BACKEND_PORT));
        this.threadsCount = Integer.parseInt(properties.getProperty(THREADS_COUNT));
    }

    public int getBackendPort() {
        return backendPort;
    }

    public int getThreadsCount() {
        return threadsCount;
    }


}
