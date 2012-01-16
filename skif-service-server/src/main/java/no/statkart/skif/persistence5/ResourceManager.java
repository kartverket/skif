package no.statkart.skif.persistence5;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence5.jdbc.ConnectionManager;

import java.util.*;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class ResourceManager implements TransactionalResource {
    private boolean inTransaction;
    private boolean isActive;

    private HashMap<Key, Entry> map = new HashMap<Key, Entry>();

    public final static class Key {
        private final String name;
        private final Class<? extends Resource> type;

        public Key(Class<? extends Resource> type) {
            this("", type);
        }

        public Key(String name, Class<? extends Resource> type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public int hashCode() {
            return name.hashCode() + type.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Key) {
                Key key = (Key) obj;
                return type == key.type && name.equals(key.name);
            }
            return false;
        }
    }

    public final static class Entry {
        final private String name;
        final private Class<? extends Resource>[] types;
        final private Resource implementation;
        private boolean transactionStarted;

        public Entry(Resource implementation) {
            this("", implementation, implementation.getClass());
        }

        public Entry(String name, Resource implementation) {
            this(name, implementation, implementation.getClass());
        }

        public Entry(Resource implementation, Class<? extends Resource>... types) {
            this("", implementation, types);
        }

        public Entry(String name, Resource implementation, Class<? extends Resource>... types) {
            this.name = name;
            this.implementation = implementation;
            this.types = types;
        }
    }

    public ResourceManager(Entry... entries) {
        for (Entry entry : entries) {
            List<Class<? extends Resource>> types = new ArrayList<Class<? extends Resource>>(Arrays.asList(entry.types));
            types.add(entry.implementation.getClass());
            for (Class<? extends Resource> type : types) {
                Key key = new Key(entry.name, type);
                Entry existing = map.put(key, entry);
                if (existing != null) {
                    throw new ImplementationException("En resource har allerede blitt for key: " + key + " entry: " + entry);
                }
            }
        }
    }

    public <T extends Resource> T getResource(Class<T> type) {
        Entry entry = map.get(new Key(type));
        if (entry == null) {
            throw new ImplementationException("Fant ingen resource av type " + type);
        }
        entry.implementation.setActive();
        if (inTransaction && entry.implementation instanceof TransactionalResource && !entry.transactionStarted) {
            TransactionalResource.class.cast(entry.implementation).beginTransaction();
            entry.transactionStarted = true;
        }
        return type.cast(entry.implementation);
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void setActive() {
        isActive = true;

    }

    @Override
    public void beginTransaction() {
        inTransaction = true;
    }

    @Override
    public void flush() {
        for (Entry entry : map.values()) {
            if (entry.implementation.isActive() && entry.implementation instanceof TransactionalResource) {
                TransactionalResource.class.cast(entry.implementation).flush();
            }
        }
    }

    @Override
    public void commit() {
        for (Entry entry : map.values()) {
            if (entry.implementation.isActive() && entry.implementation instanceof TransactionalResource) {
                TransactionalResource.class.cast(entry.implementation).commit();
            }
        }
        inTransaction = false;
        for (Entry entry : map.values()) {
            if (entry.implementation.isActive() && entry.implementation instanceof TransactionalResource) {
                entry.transactionStarted = false;
            }
        }
    }

    @Override
    public void rollback() {
        for (Entry entry : map.values()) {
            if (entry.implementation.isActive() && entry.implementation instanceof TransactionalResource) {
                TransactionalResource.class.cast(entry.implementation).rollback();
            }
        }
        inTransaction = false;
        for (Entry entry : map.values()) {
            if (entry.implementation.isActive() && entry.implementation instanceof TransactionalResource) {
                entry.transactionStarted = false;
            }
        }
    }

    @Override
    public void close() {
        for (Entry entry : map.values()) {
            if (entry.implementation.isActive()) {
                entry.implementation.close();
            }
        }
        isActive = false;
    }

}
