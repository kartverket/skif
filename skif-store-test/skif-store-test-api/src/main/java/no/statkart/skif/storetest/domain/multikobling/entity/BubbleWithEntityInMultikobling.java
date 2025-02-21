package no.statkart.skif.storetest.domain.multikobling.entity;

import no.statkart.skif.store.Components;
import no.statkart.skif.store.multikobling.DefaultKoblingFactory;
import no.statkart.skif.store.multikobling.Multikobling;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubble;

import java.util.Set;

/**
 * Boble med multikobling hvis elementer er av type {@link no.statkart.skif.store.EntityComponent}. For å
 * understøtte dette må koblingsklassen skrives litt anderledes enn for koblinger med bobleid-er og valueobjekter.
 * Se {@link EntityInMultikoblingKobling}. Bemerk at koblingsklassen har property-metoder som forwarder til
 * valueklassen for koblingen.
 *
 * @author Henrik Fredholm
 * @since 2.8.0
 */
public class BubbleWithEntityInMultikobling  extends AbstractStoreTestBubble {
    private static final long serialVersionUID = 1L;

    private Multikobling<String, EntityInMultikobling, EntityInMultikoblingKobling> multikobling = Multikobling.create(DefaultKoblingFactory.create(EntityInMultikoblingKobling.class));

    /**
     * public for testing
     */
    @SuppressWarnings("UnusedDeclaration") // For Hibernate
    public Set<EntityInMultikoblingKobling> getKoblinger() {
        return multikobling.getKoblinger();
    }

    /**
     * public for testing
     */
    @SuppressWarnings("UnusedDeclaration") // For Hibernate
    public void setKoblinger(Set<EntityInMultikoblingKobling> koblinger) {
        multikobling.setKoblinger(koblinger);
    }

    /**
     * Henter ut koblinger med rolle {@code role}
     */
    public Set<EntityInMultikobling> getEntities(String role) {
        return Components.newSet(this, multikobling.get(role));
    }
}
