package no.statkart.skif.persistence;

import no.statkart.skif.exception.ImplementationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class DefaultResourceManager implements ResourceManager {
    private boolean inTransaction;

    /** Angir om ResourceManager har vært i bruk */
    private boolean isActive;

    private HashMap<Key<?>, Entry> map = new HashMap<>();
    private Entry[] entries;
    private boolean started;

    public DefaultResourceManager(Entry... entries) {
        this.entries=entries;

        for (Entry entry : entries) {
            List<Class<? extends Resource>> types = new ArrayList<>(entry.types);
            types.add(entry.implementation.getClass());
            for (Class<? extends Resource> type : types) {
                Key<?> key = new Key<>(entry.name, type);
                Entry existing = map.put(key, entry);
                if (existing != null) {
                    throw new ImplementationException("Duplicate resource for key: " + key + ", entry: " + entry + ", existing: " + existing);
                }
            }
        }
    }

    private void checkIsStarted() {
        if (!started) {
            throw new ImplementationException("Attempted to use resource before resource manager had started");
        }
    }

    public <T extends Resource> T getResource(Class<T> type) {
        return getResource(new Key<>(type));
    }


    public <T extends Resource> T getResource(Key<T> key) {
        checkIsStarted();
        setActive();
        Entry entry = map.get(key);

        if (entry == null) {
            throw new ImplementationException("Found no resource of type " + key.type);
        }
        entry.implementation.setActive();
        ensureTransactionStarted(entry);
        return key.type.cast(entry.implementation);
    }


    private void ensureTransactionStarted(Entry entry) {
        if (inTransaction && entry.implementation instanceof TransactionalResource && !entry.transactionStarted) {
            TransactionalResource.class.cast(entry.implementation).beginTransaction();
            entry.transactionStarted = true;
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive() {
        checkIsStarted();
        isActive = true;
    }

    @Override
    public void start() {
        started = true;
    }

    @Override
    public void shutdown() {
        started = false;
    }

    @Override
    public void beginTransaction() {
        checkIsStarted();
        inTransaction = true;
        for (Entry entry : entries) {
            if (entry.implementation instanceof TransactionalResource) {
                TransactionalResource.class.cast(entry.implementation).beginTransaction();
                entry.transactionStarted = true;
            }
        }

    }

    @Override
    public void flush() {
        checkIsStarted();
        for (Entry entry : entries) {
            if (entry.implementation.isActive() && entry.implementation instanceof TransactionalResource) {
                TransactionalResource.class.cast(entry.implementation).flush();
            }
        }
    }

    @Override
    public void commit() {
        checkIsStarted();
        for (Entry entry : entries) {
            if (entry.implementation.isActive() && entry.implementation instanceof TransactionalResource) {
                ensureTransactionStarted(entry);
                TransactionalResource.class.cast(entry.implementation).commit();
            }
        }
        inTransaction = false;
        for (Entry entry : entries) {
            if (entry.implementation.isActive() && entry.implementation instanceof TransactionalResource) {
                entry.transactionStarted = false;
            }
        }
    }

    @Override
    public void rollback() {
        checkIsStarted();
        for (Entry entry : entries) {
            if (entry.implementation.isActive() && entry.implementation instanceof TransactionalResource) {
                ensureTransactionStarted(entry);
                TransactionalResource.class.cast(entry.implementation).rollback();
            }
        }
        inTransaction = false;
        for (Entry entry : entries) {
            if (entry.implementation.isActive() && entry.implementation instanceof TransactionalResource) {
                entry.transactionStarted = false;
            }
        }
    }

    @Override
    public void close() {
        checkIsStarted();
        for (Entry entry : entries) {
            if (entry.implementation.isActive()) {
                entry.implementation.close();
            }
        }
        isActive = false;
    }

}
