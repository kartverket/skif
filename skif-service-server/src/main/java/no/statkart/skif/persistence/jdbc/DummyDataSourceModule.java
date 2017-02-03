package no.statkart.skif.persistence.jdbc;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Singleton;

import javax.sql.DataSource;

/**
 * Enkel modul for å sette opp {@link DummyDataSource}.
 */
public class DummyDataSourceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DummyDataSource.class).in(Singleton.class);
        bind(Key.get(DataSource.class, NonTransactional.class)).to(DummyDataSource.class);
    }
}
