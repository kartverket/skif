package no.statkart.skif.store.persistence;

import no.statkart.skif.store.SnapshotVersion;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Henrik Fredholm
 */
public class PersistenceSessionProxyCache {
    protected final Map<Key, PersistenceSessionProxy> currentMap = new HashMap<Key, PersistenceSessionProxy>(4);
    protected final Map<Key, PersistenceSessionProxy> oldMap = new HashMap<Key, PersistenceSessionProxy>();
    protected final Map<Key, PersistenceSessionProxy> historicMap = new LinkedHashMap<Key, PersistenceSessionProxy>() {
        private final int MAX_SIZE = 100;

        @Override
        protected boolean removeEldestEntry(Map.Entry<Key, PersistenceSessionProxy> eldest) {
            return size() > MAX_SIZE;
        }
    };

    private static class Key {
        private final SnapshotVersion snapshotVersion;
        private final PersistenceSessionForSnapshot sessionForSnapshot;

        private Key(SnapshotVersion snapshotVersion, PersistenceSessionForSnapshot sessionForSnapshot) {
            this.snapshotVersion = snapshotVersion;
            this.sessionForSnapshot = sessionForSnapshot;
        }

        @Override
        public int hashCode() {
            return sessionForSnapshot.hashCode() + snapshotVersion.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            Key key = (Key) obj;
            return key.sessionForSnapshot == sessionForSnapshot && key.snapshotVersion.equals(snapshotVersion);
        }
    }

    public PersistenceSessionForSnapshot getOrCreateProxy(PersistenceSessionForSnapshot persistenceSessionForSnapshot, SnapshotVersion snapshotVersion) {
        Map<Key, PersistenceSessionProxy> map;
        if (snapshotVersion == snapshotVersion.OLD) {
            map = oldMap;
        } else if (snapshotVersion == SnapshotVersion.CURRENT) {
            map = currentMap;
        } else {
            map = historicMap;
        }
        Key key = new Key(snapshotVersion, persistenceSessionForSnapshot);
        PersistenceSessionProxy persistenceSessionProxy = map.get(key);
        if (persistenceSessionProxy == null) {
            persistenceSessionProxy = new PersistenceSessionProxy(persistenceSessionForSnapshot, snapshotVersion, this);
            map.put(key, persistenceSessionProxy);
        }
        return persistenceSessionProxy.getProxy();
    }
}
