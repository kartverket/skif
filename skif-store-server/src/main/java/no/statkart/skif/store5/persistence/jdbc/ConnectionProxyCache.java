package no.statkart.skif.store5.persistence.jdbc;

import no.statkart.skif.persistence5.jdbc.ConnectionForSnapshotVersion;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.PersistenceSessionMaster;
import no.statkart.skif.store5.persistence.hibernate.HibernatePersistenceSessionMaster;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Henrik Fredholm
 */
public class ConnectionProxyCache {
    protected ConnectionForSnapshotVersion connectionForCurrent;
    protected ConnectionForSnapshotVersion connectionForOld;
    protected final Map<PersistenceSessionMaster, ConnectionForSnapshotVersion> historicMap = new LinkedHashMap<PersistenceSessionMaster, ConnectionForSnapshotVersion>() {
        private final int MAX_SIZE = 100;

        @Override
        protected boolean removeEldestEntry(Map.Entry<PersistenceSessionMaster, ConnectionForSnapshotVersion> eldest) {
            return size() > MAX_SIZE;
        }
    };

    public ConnectionForSnapshotVersion getOrCreateProxy(HibernatePersistenceSessionMaster persistenceSessionMaster) {
        SnapshotVersion snapshotVersion = persistenceSessionMaster.getSnapshot();
        if (snapshotVersion == SnapshotVersion.CURRENT) {
            if (connectionForCurrent==null) {
                connectionForCurrent = new ConnectionProxyUsingHibernate(persistenceSessionMaster).getProxy();
            }
            return connectionForCurrent;
        } else if (snapshotVersion == SnapshotVersion.OLD) {
            if (connectionForOld== null) {
                connectionForOld = new ConnectionProxyUsingHibernate(persistenceSessionMaster).getProxy();
            }
            return connectionForOld;
        } else {
            ConnectionForSnapshotVersion connection = historicMap.get(persistenceSessionMaster);
            if (connection==null) {
                connection = new ConnectionProxyUsingHibernate(persistenceSessionMaster).getProxy();
                historicMap.put(persistenceSessionMaster, connection);
            }
            return connection;
        }
    }
}
