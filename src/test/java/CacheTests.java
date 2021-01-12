import Entities.Testing;
import ORM.Cache;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class CacheTests {

    @Test
    public void cacheInitTest() {
        Cache cache = new Cache();
        Assertions.assertNotNull(cache);
    }

    @Test
    public void cacheSetCacheTest() {
        Map<Class<?>, Cache> caches = new HashMap<>();
        caches.put(Testing.class, new Cache());
        Assertions.assertTrue(caches.containsKey(Testing.class));
    }

    @Test
    public void cacheSetEntryTest() {
        Map<Class<?>, Cache> caches = new HashMap<>();
        Testing test = new Testing(15, "this string will be ignored", "35 char text.");
        caches.put(Testing.class, new Cache());
        caches.get(Testing.class).setEntry(15,test);
        Assertions.assertTrue(caches.get(Testing.class).contains(15));
    }

    @Test
    public void cacheGetEntryTest() {
        Map<Class<?>, Cache> caches = new HashMap<>();
        Testing test = new Testing(15, "this string will be ignored", "35 char text.");
        caches.put(Testing.class, new Cache());
        caches.get(Testing.class).setEntry(15,test);
        Assertions.assertEquals(caches.get(Testing.class).getEntry(15),test);
    }

    @Test
    public void cacheDeleteEntryTest() {
        Map<Class<?>, Cache> caches = new HashMap<>();
        Testing test = new Testing(15, "this string will be ignored", "35 char text.");
        caches.put(Testing.class, new Cache());
        caches.get(Testing.class).setEntry(15,test);
        Assertions.assertTrue(caches.get(Testing.class).contains(15));
        caches.get(Testing.class).deleteEntry(15);
        Assertions.assertFalse(caches.get(Testing.class).contains(15));
    }
}
