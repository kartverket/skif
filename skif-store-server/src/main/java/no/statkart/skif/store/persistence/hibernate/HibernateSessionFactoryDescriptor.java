package no.statkart.skif.store.persistence.hibernate;

import com.google.inject.Provider;
import com.google.inject.util.Providers;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.config.SkifConfigConstants;
import no.statkart.skif.exception.ImplementationException;
import com.google.common.base.Preconditions;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;
import org.hibernate.Interceptor;
import org.hibernate.Session;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

/**
 * @author Henrik Fredholm
 */
public class HibernateSessionFactoryDescriptor {
    private final String name;
    private final SnapshotVersionSeed seed;
    private final SnapshotVersion initialSeedValue;
    private final boolean setSnapshotOnSession;
    private final Properties hibernateProperties;
    private final HibernateInterceptorFactory hibernateInterceptorFactory;
    private final boolean isSnapshotChangable;

    public HibernateSessionFactoryDescriptor(String name, SnapshotVersionSeed seed, Properties hibernateProperties) {
        this(name, seed, false, false, hibernateProperties);
    }

    public HibernateSessionFactoryDescriptor(String name, SnapshotVersionSeed seed, boolean setSnapshotOnSession, boolean isSnapshotChangable, Properties hibernateProperties) {
        this(name, seed, setSnapshotOnSession, isSnapshotChangable, hibernateProperties, new NullHibernateInterceptorFactory());
    }

    public HibernateSessionFactoryDescriptor(String name, SnapshotVersionSeed seed, boolean setSnapshotOnSession, boolean isSnapshotChangable, Properties hibernateProperties, HibernateInterceptorFactory hibernateInterceptorFactory) {
        this.name = name;
        this.seed = seed;
        this.initialSeedValue = seed.get();
        this.hibernateProperties = hibernateProperties;
        this.hibernateInterceptorFactory = hibernateInterceptorFactory;
        this.setSnapshotOnSession = setSnapshotOnSession;
        this.isSnapshotChangable = isSnapshotChangable;

    }

    public boolean accepts(SnapshotVersion snapshotVersion) {
        if (seed.get() == SnapshotVersion.CURRENT) {
            return snapshotVersion == SnapshotVersion.CURRENT;
        } else {
            return snapshotVersion != SnapshotVersion.CURRENT;
        }
    }

    public SnapshotVersion getSnapshotVersion() {
        return seed.get();
    }


    public void setSnapshotVersion(Session session, SnapshotVersion snapshotVersion) {
        Preconditions.checkArgument(accepts(snapshotVersion), "Session " + name + " støtter ikke snapshot version: " + snapshotVersion);
        if (setSnapshotOnSession) {
            session.createSQLQuery("select snapshot_time.set_t(:timestamp) from dual").setTimestamp("timestamp", snapshotVersion.getTimestamp()).executeUpdate();
        }
        seed.set(snapshotVersion);
    }

    public void resetSeed() {
        if (!seed.get().equals(initialSeedValue)) {
            seed.set(initialSeedValue);
        }
    }

    public String getName() {
        return name;
    }

    public SnapshotVersionSeed getSeed() {
        return seed;
    }

    public Properties getHibernateProperties() {
        return hibernateProperties;
    }

    public HibernateInterceptorFactory getHibernateInterceptorFactory() {
        return hibernateInterceptorFactory;
    }

    public boolean isSetSnapshotOnSession() {
        return setSnapshotOnSession;
    }

    public boolean isSnapshotChangable() {
        return isSnapshotChangable;
    }
}
