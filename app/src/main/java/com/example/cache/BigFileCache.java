package com.example.cache;

import android.util.Log;
import android.util.LruCache;

import java.util.LinkedHashMap;
import java.util.Map;

public class BigFileCache<K, V> {
    private final LinkedHashMap<K, V> map;

    /** Size of this cache in units. Not necessarily the number of elements. */
    private int size;
    private int maxSize;

    private int putCount;
    private int hitCount;
    private int missCount;

    private int bigFileCacheMaxSize;
    private int bigFileLowThreshold;
    private int bigFileHighThreshold;
    private final LruCache<K, V> bigFileCache;

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *     the maximum number of entries in the cache. For all other caches,
     *     this is the maximum sum of the sizes of the entries in this cache.
     */
    public BigFileCache(int maxSize, int bigFileCacheMaxSize, int bigFileLowThreshold, int bigFileHighThreshold) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        }
        if (bigFileCacheMaxSize < 0) {
            throw new IllegalArgumentException("bigFileCacheMaxSize < 0");
        }
        if (bigFileLowThreshold < 0) {
            throw new IllegalArgumentException("bigFileLowThreshold < 0");
        }
        if (bigFileHighThreshold < 0 || bigFileHighThreshold > bigFileCacheMaxSize) {
            throw new IllegalArgumentException("bigFileHighThreshold < 0 or bigFileHighThreshold > bigFileCacheMaxSize");
        }
        this.maxSize = maxSize;
        this.bigFileCacheMaxSize = bigFileCacheMaxSize;
        this.bigFileLowThreshold = bigFileLowThreshold;
        this.bigFileHighThreshold = bigFileHighThreshold;
        this.map = new LinkedHashMap<K, V>(0, 0.75f, true);
        this.bigFileCache = new LruCache<K, V>(bigFileCacheMaxSize){
            @Override
            protected int sizeOf(K key, V value) {
                return BigFileCache.this.sizeOf(key, value);
            }

            @Override
            protected void entryRemoved(boolean evicted, K key, V oldValue, V newValue) {
                if(evicted) {
                    Log.d("hhh", "大图片队列满");
                    BigFileCache.this.map.remove(key);
                }
            }
        };
    }

    /**
     * Returns the value for {@code key} if it exists in the cache or can be
     * created by {@code #create}. If a value was returned, it is moved to the
     * head of the queue. This returns null if a value is not cached and cannot
     * be created.
     */
    public final V get(K key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        V mapValue;
        synchronized (this) {
            mapValue = map.get(key);
            if (mapValue != null) {
                hitCount++;
                if(safeSizeOf(key, mapValue) > bigFileLowThreshold) {
                    bigFileCache.get(key);
                }
                return mapValue;
            }
            missCount++;
        }
        return null;
    }

    /**
     * Caches {@code value} for {@code key}. The value is moved to the head of
     * the queue.
     *
     * @return the previous value mapped by {@code key}.
     */
    public final V put(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException("key == null || value == null");
        }

        V previous;
        synchronized (this) {
            if(safeSizeOf(key, value) > bigFileLowThreshold && safeSizeOf(key, value) < bigFileHighThreshold) {
                bigFileCache.put(key, value);
            }
            else if (safeSizeOf(key, value) >= bigFileHighThreshold){
                return null;
            }
            putCount++;
            size += safeSizeOf(key, value);
            previous = map.put(key, value);
            if (previous != null) {
                size -= safeSizeOf(key, previous);
            }
        }

        trimToSize(maxSize);
        return previous;
    }

    /**
     * @param maxSize the maximum size of the cache before returning. May be -1
     *     to evict even 0-sized elements.
     */
    private void trimToSize(int maxSize) {
        while (true) {
            K key;
            V value;
            synchronized (this) {
                if (size < 0 || (map.isEmpty() && size != 0)) {
                    throw new IllegalStateException(getClass().getName()
                            + ".sizeOf() is reporting inconsistent results!");
                }

                if (size <= maxSize) {
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
                size -= safeSizeOf(key, value);
                if(safeSizeOf(key, value) > bigFileLowThreshold) {
                    bigFileCache.remove(key);
                }
            }
        }
    }

    /**
     * Removes the entry for {@code key} if it exists.
     *
     * @return the previous value mapped by {@code key}.
     */
    public final V remove(K key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        V previous;
        synchronized (this) {
            previous = map.remove(key);
            if (previous != null) {
                size -= safeSizeOf(key, previous);
            }
        }

        return previous;
    }

    private int safeSizeOf(K key, V value) {
        int result = sizeOf(key, value);
        if (result < 0) {
            throw new IllegalStateException("Negative size: " + key + "=" + value);
        }
        return result;
    }

    /**
     * Returns the size of the entry for {@code key} and {@code value} in
     * user-defined units.  The default implementation returns 1 so that size
     * is the number of entries and max size is the maximum number of entries.
     *
     * <p>An entry's size must not change while it is in the cache.
     */
    protected int sizeOf(K key, V value) {
        return 1;
    }

    /**
     * For caches that do not override {@link #sizeOf}, this returns the number
     * of entries in the cache. For all other caches, this returns the sum of
     * the sizes of the entries in this cache.
     */
    public synchronized final int size() {
        return size;
    }

    public synchronized final int bigFileCacheSize() {
        return bigFileCache.size();
    }
    /**
     * For caches that do not override {@link #sizeOf}, this returns the maximum
     * number of entries in the cache. For all other caches, this returns the
     * maximum sum of the sizes of the entries in this cache.
     */
    public synchronized final int maxSize() {
        return maxSize;
    }

    /**
     * Returns the number of times {@link #get} returned a value that was
     * already present in the cache.
     */
    public synchronized final int hitCount() {
        return hitCount;
    }

    /**
     * Returns the number of times {@link #get} returned null or required a new
     * value to be created.
     */
    public synchronized final int missCount() {
        return missCount;
    }

    /**
     * Returns the number of times {@link #put} was called.
     */
    public synchronized final int putCount() {
        return putCount;
    }
}
