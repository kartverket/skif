package no.statkart.skif.storetest.service.storetest1;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.storetest.domain.TestBubble;
import no.statkart.skif.storetest.domain.TestBubbleId;
import org.hibernate.Session;

import java.util.HashMap;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class StoreTest1ServiceImpl implements StoreTest1Service {
    static HashMap<String, String> map = new HashMap<String, String>();

    @Inject
    Provider<Session> sessionProvider;

    @Override
    public String put(String key, String value) {
        return map.put(key, value);
    }

    @Override
    public String get(String key) {
        Session s = sessionProvider.get();
        TestBubble e = (TestBubble) s.get(TestBubble.class, new TestBubbleId(1));
        if (e == null) {
            throw new RuntimeException("Skal ikke kommer her");
        }

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
