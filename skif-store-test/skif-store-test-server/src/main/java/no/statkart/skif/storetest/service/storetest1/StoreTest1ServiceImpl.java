package no.statkart.skif.storetest.service.storetest1;

import java.util.HashMap;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class StoreTest1ServiceImpl implements StoreTest1Service {
    static HashMap<String,String> map = new HashMap<String,String>();

    @Override
    public String put(String key, String value) {
        return map.put(key, value);
    }

    @Override
    public String get(String key) {
        return map.get(key);
    }

    @Override
    public String remove(String key) {
        return map.remove(key);
    }

    @Override
    public void clear() {
        map.clear();
    }
}
