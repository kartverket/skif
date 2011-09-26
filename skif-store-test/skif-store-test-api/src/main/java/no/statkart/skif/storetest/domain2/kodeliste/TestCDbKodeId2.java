package no.statkart.skif.storetest.domain2.kodeliste;

import no.statkart.skif.store.ReplicaVersion;
import no.statkart.skif.store2.BubbleObject2;
import no.statkart.skif.store2.ReplicaVersion2;
import no.statkart.skif.store2.kodelistesupport2.DbKodeIdImpl2;
import no.statkart.skif.store2.kodelistesupport2.DbSubclassedKodeId2;
import no.statkart.skif.storetest.domain.kodeliste.TestCDbKode;
import no.statkart.skif.storetest.domain.kodeliste.impl.DbSubclassedKodeId;
import no.statkart.skif.storetest.domain2.TestDbKodeId2;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public abstract class TestCDbKodeId2<T extends TestCDbKode2> extends DbKodeIdImpl2<T> implements DbSubclassedKodeId2<T>, TestDbKodeId2<T> {

    protected TestCDbKodeId2(Long value, ReplicaVersion2 replicaVersion) {
        super(value, replicaVersion);
    }

}
