package no.statkart.skif.persistence;

import no.statkart.skif.exception.ImplementationException;

import java.util.Map;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class SwitchingResourceManager implements ResourceManager {
    private ResourceManager selected;
    private final ResourceManager jdbcOnlyBased;
    private final ResourceManager hibernateBased;

    public SwitchingResourceManager(ResourceManager jdbcOnlyBased, ResourceManager hibernateBased) {
        this.jdbcOnlyBased = jdbcOnlyBased;
        this.hibernateBased = hibernateBased;
    }


    private ResourceManager selected() {
        if (selected==null) {
            selected = jdbcOnlyBased;
        }
        return selected();
    }

    public void selectHibernateBased() {
        if (selected!=hibernateBased && selected==null) {
            selected = hibernateBased;
        } else {
            throw new ImplementationException("JDBC bassert ResourceManager har allerede blitt valgt");
        }
    }

    @Override
    public <T extends Resource> T getResource(Class<T> type) {
        return selected().getResource(type);
    }

    @Override
    public <T extends Resource> T getResource(Key key) {
        return selected().getResource(key);
    }

    @Override
    public boolean isActive() {
        return selected().isActive();
    }

    @Override
    public void setActive() {
        selected().setActive();
    }

    @Override
    public void beginTransaction() {
        selected().beginTransaction();
    }

    @Override
    public void flush() {
        selected().flush();
    }

    @Override
    public void commit() {
        selected().commit();
    }

    @Override
    public void rollback() {
        selected().rollback();
    }

    @Override
    public void close() {
        selected().close();
    }
}
