package no.statkart.skif.store3.persistence.hibernate;

import com.google.inject.Provider;
import com.google.inject.util.Providers;
import no.statkart.skif.guava.Preconditions;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;
import no.statkart.skif.store3.persistence.PersistenceDescriptorBaseImpl;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.Properties;

/**
 * @author Henrik Fredholm
 */
public class HibernateSessionFactoryDescriptor extends PersistenceDescriptorBaseImpl<SessionFactory> {
    private final Properties hiberanteProperties;
    private final Provider<Interceptor> hibernateInterceptorProvider;
    private final boolean isCurrent;
    private final boolean setSnapshotOnSession;

    public HibernateSessionFactoryDescriptor(String name, SnapshotVersionSeed seed, boolean setSnapshotOnSession, Properties hiberanteProperties) {
        this(name, seed, setSnapshotOnSession, hiberanteProperties, Providers.<Interceptor>of(null));
    }

    public HibernateSessionFactoryDescriptor(String name, SnapshotVersionSeed seed, boolean setSnapshotOnSession, Properties hiberanteProperties, Provider<Interceptor> hibernateInterceptorProvider) {
        super(name, seed);
        this.hiberanteProperties = hiberanteProperties;
        this.hibernateInterceptorProvider = hibernateInterceptorProvider;
        this.isCurrent = (seed.get() == SnapshotVersion.CURRENT);
        this.setSnapshotOnSession = setSnapshotOnSession;
    }

    public Interceptor getHibernateInterceptor() {
        return hibernateInterceptorProvider.get();
    }

    public Properties getHibernateProperties() {
        return hiberanteProperties;
    }

    public boolean accepts(SnapshotVersion snapshotVersion) {
        if (isCurrent) {
            return snapshotVersion == SnapshotVersion.CURRENT;
        } else {
            return snapshotVersion == snapshotVersion.OLD || (setSnapshotOnSession && snapshotVersion!=SnapshotVersion.CURRENT);
        }
    }

    public void setSnapshotVersion(Session session, SnapshotVersion snapshotVersion) {
        Preconditions.checkArgument(accepts(snapshotVersion), "Session " + getName() + " støtter ikke snapshot version: " + snapshotVersion);
        if (setSnapshotOnSession) {
            session.createSQLQuery("select snapshot_time.set_t(:timestamp) from dual").setTimestamp("timestamp", snapshotVersion.getTimestamp()).executeUpdate();
        }
    }
}
