package no.statkart.skif.store.persistence.hibernate;

import com.google.inject.Inject;
import no.statkart.skif.store.SnapshotVersionSeed;
import org.hibernate.Interceptor;

import javax.annotation.Nullable;

/**
 * Factory for å opprette Hibernate Interceptor.
 * <p>
 * Implementasjoner av dette interface kan anvende dependency injection ved bruk av et standard injection builder pattern,
 * dvs at implementasjonen definere en constructor som å få injected ekstra
 * parametre, f.eks i form av {@code Provider} instanser som gjemmes som member variable. I {@link #create}
 * opprettes {@code Interceptor} instansen manuelt via {@code new} med de parametre som constructoren trenger. Disses
 * parametre hentes fra member variable. Ved å bruke {@code Provider} baserte member variable gir man mulighet for at
 * hver interceptor som blir opprette ikke deler objekter - avhengig av hvordan scope for provideren er definert.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface HibernateInterceptorFactory {
    /**
     * Opprett injector for gitt snapshotVersionSeed
     * @return
     */
    @Nullable
    Interceptor create(SnapshotVersionSeed snapshotVersionSeed);
}
