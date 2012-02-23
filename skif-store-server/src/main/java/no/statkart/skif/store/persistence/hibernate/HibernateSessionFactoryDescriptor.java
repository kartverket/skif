package no.statkart.skif.store.persistence.hibernate;

import com.google.inject.Provider;
import com.google.inject.util.Providers;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.config.SkifConfigConstants;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.guava.Preconditions;
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
    private final Provider<Interceptor> hibernateInterceptorProvider;
    private final boolean isSnapshotChangable;

    public HibernateSessionFactoryDescriptor(String name, SnapshotVersionSeed seed, Properties hiberanteProperties) {
        this(name, seed, false, false, hiberanteProperties, Providers.<Interceptor>of(null));
    }

    public HibernateSessionFactoryDescriptor(String name, SnapshotVersionSeed seed, boolean setSnapshotOnSession, boolean isSnapshotChangable, Properties hiberanteProperties) {
        this(name, seed, setSnapshotOnSession, isSnapshotChangable, hiberanteProperties, Providers.<Interceptor>of(null));
    }

    public HibernateSessionFactoryDescriptor(String name, SnapshotVersionSeed seed, boolean setSnapshotOnSession, boolean isSnapshotChangable, Properties hiberanteProperties, Class<? extends HibernateStoreInterceptor> interceptorClass) {
        this(name, seed, setSnapshotOnSession, isSnapshotChangable, hiberanteProperties, createInterceptorProvider(interceptorClass, seed));
    }

    private static Provider<Interceptor> createInterceptorProvider(final Class<? extends Interceptor> interceptorClass, final SnapshotVersionSeed seed) {
        try {
            final Constructor<? extends Interceptor> constructor = interceptorClass.getConstructor(SnapshotVersionSeed.class);
            return new Provider<Interceptor>() {
                @Override
                public Interceptor get() {
                    try {
                        return constructor.newInstance(seed);
                    } catch (InstantiationException e) {
                        throw new ImplementationException(e);
                    } catch (IllegalAccessException e) {
                        throw new ImplementationException(e);
                    } catch (InvocationTargetException e) {
                        throw new ImplementationException(e);
                    }
                }
            };
        } catch (NoSuchMethodException e) {
            throw new ImplementationException("Manglende constructor", e);
        }
    }


    public HibernateSessionFactoryDescriptor(String name, SnapshotVersionSeed seed, boolean setSnapshotOnSession, boolean isSnapshotChangable, Properties hibernateProperties, Provider<Interceptor> hibernateInterceptorProvider) {
        this.name = name;
        this.seed = seed;
        this.initialSeedValue = seed.get();
        this.hibernateProperties = hibernateProperties;
        this.hibernateInterceptorProvider = hibernateInterceptorProvider;
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

    public Provider<Interceptor> getHibernateInterceptorProvider() {
        return hibernateInterceptorProvider;
    }

    public boolean isSetSnapshotOnSession() {
        return setSnapshotOnSession;
    }

    public boolean isSnapshotChangable() {
        return isSnapshotChangable;
    }
}
