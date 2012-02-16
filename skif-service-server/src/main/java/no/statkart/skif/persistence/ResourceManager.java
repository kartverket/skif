package no.statkart.skif.persistence;

import no.statkart.skif.exception.ImplementationException;

import java.util.*;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface ResourceManager extends TransactionalResource {
    public final static class Key {
        final String name;
        final Class<? extends Resource> type;

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
        final String name;
        final Class<? extends Resource>[] types;
        final Resource implementation;

        /** Angir om denne resources har fått startet sin transaksjon */
        boolean transactionStarted;

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


    public <T extends Resource> T getResource(Class<T> type);

    public <T extends Resource> T getResource(Key key);

    public boolean isActive();

    public void setActive();

    public void beginTransaction();

    public void flush();

    public void commit();

    public void rollback();

    public void close();
}
