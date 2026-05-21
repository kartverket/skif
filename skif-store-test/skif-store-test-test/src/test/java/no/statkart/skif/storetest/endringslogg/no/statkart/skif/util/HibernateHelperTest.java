package no.statkart.skif.storetest.endringslogg.no.statkart.skif.util;

import com.google.inject.Inject;
import jakarta.inject.Provider;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.SessionSelector;
import no.statkart.skif.storetest.domain.basic.Simple;
import no.statkart.skif.storetest.domain.basic.SubTypeWithPrimitive;
import no.statkart.skif.storetest.domain.basic.SubTypedBubble;
import no.statkart.skif.storetest.util.testsupport.StoreTestServerTestCase;
import no.statkart.skif.util.HibernateHelper;
import org.hibernate.persister.entity.EntityPersister;
import org.testng.annotations.Test;

import static no.statkart.skif.util.HibernateHelper.getPersister;
import static org.assertj.core.api.Assertions.assertThat;

public class HibernateHelperTest extends StoreTestServerTestCase {
    @Inject
    Provider<SessionSelector> sessionSelectorProvider;

    @Test
    public void getDiscriminatorSql_for_baseclass_returns_blank() {
        try (var sessionSelector = sessionSelectorProvider.get()) {
            var session = sessionSelector.get(SnapshotVersion.CURRENT);
            EntityPersister simplePersister = getPersister(session, Simple.class);
            EntityPersister subTypedBubblePersister = getPersister(session, SubTypedBubble.class);

            assertThat(HibernateHelper.getDiscriminatorSql(simplePersister, "t")).isBlank();
            assertThat(HibernateHelper.getDiscriminatorSql(subTypedBubblePersister, "t")).isBlank();
        }
    }

    @Test
    public void getDiscriminatorSql_for_subclass_returns_discriminator() {
        try (var sessionSelector = sessionSelectorProvider.get()) {
            var session = sessionSelector.get(SnapshotVersion.CURRENT);
            EntityPersister subtypeBubblePersister = getPersister(session, SubTypeWithPrimitive.class);
            assertThat(HibernateHelper.getDiscriminatorSql(subtypeBubblePersister, "t"))
                .isEqualTo(" and t.class='SubTypeWithPrimitive'");
        }
    }
}
