package ORM;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

/**
 * Own class for cache objects so instances of filled custom database objects can be cached.
 * Expands the simple entity cache in Manager, which only stores the empty entity objects of custom classes for runtime efficiency.
 */
public class Cache {
    /**
     * Cache logger
     */
    final static Logger cacheLogger = LogManager.getLogger("Cache");
    /**
     * HashMap containing cached entity objects.
     */
    private final HashMap<Object, Object> cache;

    /**
     * creates the cache hashMap.
     */
    protected Cache () {
        this.cache = new HashMap<>();
    }

    /**
     * Checks for certain key in cache hashMap.
     * @param pk    the key which will be searched for in the cache hashMap.
     * @return      value for primary key input if exists.
     */
    public Object getEntry (Object pk) {
        Object object = this.cache.get(pk);
        if(object != null) {
            cacheLogger.info(object.toString() + "Object is cached - retrieved from objectCache, cached PK: " + pk.toString());
        }
        return object;
    }

    /**
     * Method for inserting a new key-value pair into the cache hashMap.
     * @param pk    the unique key, for which the entry will be searchable.
     * @param value the value, which will be stored for the unique key.
     */
    public void setEntry (Object pk, Object value) {
        this.cache.put(pk,value);
    }

    /**
     * Checks if a certain primary key Object is listed within the cache hashMap.
     * @param pk    the key Object for which will be searched in the cache hashMap.
     * @return      true, if cache contains certain key, else false.
     */
    public boolean contains (Object pk) {
        return cache.containsKey(pk);
    }

    /**
     * removes key-value pair from cache hasMap if existing.
     * @param pk    the key to determine which kv-pair will be deleted.
     */
    public void deleteEntry (Object pk) {
        cache.remove(pk);
    }
}
