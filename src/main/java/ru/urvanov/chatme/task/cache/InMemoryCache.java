package ru.urvanov.chatme.task.cache;

import java.lang.ref.SoftReference;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
 
public class InMemoryCache implements Cache<String, byte[]> {
 
    private static final int CLEAN_UP_PERIOD_IN_SEC = 5;
 
    private final ConcurrentHashMap<String, SoftReference<CacheObject<byte[]>>> cache = new ConcurrentHashMap<>();
 
    private Thread cleanerThread;
    private int cacheMillis;
    
    public InMemoryCache(int cacheMillis) {
        this.cacheMillis = cacheMillis;
        cleanerThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(CLEAN_UP_PERIOD_IN_SEC * 1000);
                    cache.entrySet().removeIf(entry -> Optional.ofNullable(entry.getValue()).map(SoftReference::get).map(CacheObject::isExpired).orElse(false));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        cleanerThread.setDaemon(true);
        cleanerThread.start();
    }
    
    public void stop() {
        cleanerThread.interrupt();
    }
 
    @Override
    public void add(String key, byte[] value) {
        if (key == null) {
            return;
        }
        if (value == null) {
            cache.remove(key);
        } else {
            long expiryTime = System.currentTimeMillis() + cacheMillis;
            cache.put(key, new SoftReference<>(new CacheObject<>(value, expiryTime)));
        }
    }
 
    @Override
    public void remove(String key) {
        cache.remove(key);
    }
 
    @Override
    public byte[] get(String key) {
        return Optional.ofNullable(cache.get(key)).map(SoftReference::get).filter(cacheObject -> !cacheObject.isExpired()).map(CacheObject::getValue).orElse(null);
    }
 
    @Override
    public void clear() {
        cache.clear();
    }
 
    @Override
    public long size() {
        return cache.entrySet().stream().filter(entry -> Optional.ofNullable(entry.getValue()).map(SoftReference::get).map(cacheObject -> !cacheObject.isExpired()).orElse(false)).count();
    }
 
    private static class CacheObject<V> {
 
        private V value;
        private long expiryTime;
 
        public CacheObject(V value, long expiryTime) {
            this.value = value;
            this.expiryTime = expiryTime;
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }

        public V getValue() {
            return value;
        }
    }
}