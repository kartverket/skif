package no.statkart.skif.storetest.domain2;

import no.statkart.skif.store2.ReplicaVersion2;
import no.statkart.skif.store2.kodelistesupport2.*;

/**
 * @author Roar Ingebrigtsen
 * @since 2.5
 */
public class TestDbKodelisteIdImpl2<T extends TestDbKodelisteImpl2> extends DbKodelisteIdImpl2<T> implements TestDbKodelisteId2<T> {

    public TestDbKodelisteIdImpl2(Long value, ReplicaVersion2 replicaVersion) {
        super(value, replicaVersion);
    }

}
