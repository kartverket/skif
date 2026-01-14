package no.statkart.skif.store.persistence.hibernate;

import com.google.common.base.Preconditions;
import no.statkart.skif.persistence.hibernate.type.OracleLocalTimestamp;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.CustomType;
import org.hibernate.type.spi.TypeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @author Henrik Fredholm
 */
public class HibernateSessionFactoryDescriptor {
    protected static Logger logger = LoggerFactory.getLogger( HibernateSessionFactoryDescriptor.class );
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
        return isSnapshotChangable() || seed.get().equals(snapshotVersion);
    }

    public SnapshotVersion getSnapshotVersion() {
        return seed.get();
    }


    public void setSnapshotVersion(Session session, SnapshotVersion snapshotVersion) {
        Preconditions.checkArgument(accepts(snapshotVersion), "Session " + name + " støtter ikke snapshot version: " + snapshotVersion);
        if (logger.isTraceEnabled()) {
            logger.trace( name + ": setting Hibernate session " + System.identityHashCode(session) + " to use SnapshotVersion " + snapshotVersion.getTimestampString() +
                                  ( ( setSnapshotOnSession ) ?
                                          " (will execute an update on database session)" :
                                          " (database session will NOT be updated as this has been disabled for this descriptor)" ) );
        }
        if (setSnapshotOnSession) {
            SessionImplementor sessionImplementor = (SessionImplementor) session;
            TypeConfiguration typeConfiguration = sessionImplementor.getSessionFactory().getTypeConfiguration();
            CustomType<?> localTimestampType = new CustomType<>(new OracleLocalTimestamp(), typeConfiguration);
            session.createNativeQuery("select snapshot_time.set_t(:timestamp) as result from dual")
                    .addScalar("result", localTimestampType)
                    .setParameter("timestamp", snapshotVersion.getTimestamp(), localTimestampType)
                    .uniqueResult();
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
