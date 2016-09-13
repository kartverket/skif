package no.statkart.skif.store.multikobling;

import no.statkart.skif.store.EntityComponent;
import org.hibernate.collection.PersistentSet;
import org.hibernate.engine.SessionImplementor;

import java.util.Map;
import java.util.Set;

/**
 * Utvider Hibernates {@link PersistentSet} til å håndtere {@link Multikobling}-er for {@link EntityComponent}-elementer
 * korrekt. Hvis en kobling fjernes via {@link #remove} for så senere å bli opprettet med en annen rolle, så sikre denne
 * implementasjonen at entity-instansen for koblingen blir gjenbrukt. Klassen er designet for bli brukt sammen med
 * {@link Multikobling} som kun krever funksjonaliteten ifm {@link #add}-operasjoner.
 *
 * @see EntityKobling
 * @see Multikobling
 * @author Henrik Fredholm
 * @since 2.8.0
 */
public class MultikoblingPersistentSet extends PersistentSet {
    private static final long serialVersionUID = 1L;

    public MultikoblingPersistentSet(SessionImplementor session) {
        super(session);
    }

    public MultikoblingPersistentSet(SessionImplementor session, Set set) {
        super(session, set);
    }

    @Override
    public boolean add(Object value) {
        Object resolvedValue = useInstanceFromSnapshotIfPresent((EntityKobling<?, ?>) value);
        return super.add(resolvedValue);
    }

    private <K, V extends EntityComponent> Object useInstanceFromSnapshotIfPresent(EntityKobling<K, V> value) {
        if (getSession()!=null) {
            java.util.Map snapshot = (Map) getSnapshot();
            EntityKobling<K, V> attachedInstance = (EntityKobling<K, V>) snapshot.get(value);
            if (attachedInstance != null) {
                attachedInstance.setRolle(value.getRolle());
                attachedInstance.setValue(value.getValue());
                return attachedInstance;
            }
        }
        return value;
    }
}
