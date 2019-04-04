package com.example.cache;

import java.util.LinkedHashMap;
import java.util.Map;

public class TwoQCache<K, V> {
    private int maxSize;
    private int FIFOMaxSize;
    private int LRUMaxSize;
    private final float DEFAULT_LOAD_FACTORY = 0.75f;
    private int FIFOSize;
    private int LRUSize;
    private int putCount;
    private int hitCount;
    private int missCount;

    private LinkedHashMap<K, V> FIFOQueue;
    private LinkedHashMap<K, V> LRUQueue;

    public TwoQCache(int maxSize) {
        this.maxSize = maxSize;
        FIFOMaxSize = maxSize/2;
        LRUMaxSize = maxSize/2;

        FIFOQueue = new LinkedHashMap<K, V>(0, DEFAULT_LOAD_FACTORY, false);
        LRUQueue = new LinkedHashMap<K, V>(0, DEFAULT_LOAD_FACTORY, true);
    }

    public int size() {
        return LRUSize + FIFOSize;
    }

    public int putCount() {
        return putCount;
    }

    public int hitCount() {
        return hitCount;
    }

    public int missCount() {
        return missCount;
    }

    protected int sizeOf(K key, V value) {
        return 1;
    }

    private void trimToSize(Map<K, V> map, boolean isFIFO) {
        while (true) {
            K key;
            V value;
            synchronized (this) {
                if ((isFIFO && (FIFOSize < 0 || (map.isEmpty() && FIFOSize != 0)))
                || (!isFIFO && (LRUSize < 0 || (map.isEmpty() && LRUSize != 0)))) {
                    throw new IllegalStateException(getClass().getName()
                            + ".sizeOf() is reporting inconsistent results!");
                }

                if ((isFIFO && FIFOSize <= FIFOMaxSize) ||
                        (!isFIFO && LRUSize <= LRUMaxSize)) {
                    break;
                }

                // BEGIN LAYOUTLIB CHANGE
                // get the last item in the linked list.
                // This is not efficient, the goal here is to minimize the changes
                // compared to the platform version.
                Map.Entry<K, V> toEvict = null;
                for (Map.Entry<K, V> entry : map.entrySet()) {
                    toEvict = entry;
                }
                // END LAYOUTLIB CHANGE

                if (toEvict == null) {
                    break;
                }

                key = toEvict.getKey();
                value = toEvict.getValue();
                map.remove(key);
                if(isFIFO) FIFOSize -= sizeOf(key, value);
                else LRUSize -= sizeOf(key, value);
            }

        }
    }

    public V put(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException("key == null || value == null");
        }
        V previous;
        synchronized (this) {
            putCount++;
            FIFOSize += sizeOf(key, value);
            previous = FIFOQueue.put(key, value);
            if (previous != null) {
                FIFOSize -= sizeOf(key, previous);
            }
        }
        trimToSize(FIFOQueue, true);
        return previous;
    }

    public V get(K key) {
        V value = LRUQueue.get(key);
        if(value == null) {
            value = FIFOQueue.get(key);
            if(value != null) {
                hitCount++;
                LRUQueue.put(key, value);
                LRUSize += sizeOf(key, value);
                FIFOQueue.remove(key);
                FIFOSize -= sizeOf(key, value);
                trimToSize(LRUQueue, false);
            }
            else missCount++;
            return  value;
        }
        hitCount++;
        return value;
    }
}
