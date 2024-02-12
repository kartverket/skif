package no.statkart.skif.storetest.wsapi.mapping;

import com.google.inject.util.Providers;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

public class KodeMappingTest {
    @Test
    public void mapCKodeId() {
        StoreTestMapper mapper = new StoreTestMapper(Providers.of(SnapshotVersion.CURRENT));
        StoreTestMapping mapping = mapper.getMapping();

        no.statkart.skif.storetest.domain.demo.koder.CDbKodeId<?> domainKodeId = new no.statkart.skif.storetest.domain.demo.koder.CDbKodeId<>(1234L, SnapshotVersion.CURRENT);

        StoreTestBubbleId wsKodeId = mapping.d2w(domainKodeId, StoreTestBubbleId.class);

        no.statkart.skif.storetest.domain.StoreTestBubbleId<?> resultKodeId = mapping.w2d(wsKodeId, no.statkart.skif.storetest.domain.StoreTestBubbleId.class);

        Assertions.assertThat(resultKodeId)
            .isInstanceOf(no.statkart.skif.storetest.domain.demo.koder.CDbKodeId.class)
            .extracting(no.statkart.skif.store.BubbleId::getValue)
            .isEqualTo(1234L);
    }
}
