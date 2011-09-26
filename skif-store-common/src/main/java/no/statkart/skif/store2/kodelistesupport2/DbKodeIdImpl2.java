package no.statkart.skif.store2.kodelistesupport2;

import no.statkart.skif.store2.ReplicaVersion2;

/**
 * @author Roar Ingebrigtsen
 * @since 2.5
 */
public abstract class DbKodeIdImpl2<T extends DbKodeImpl2> extends KodeIdImpl2<T> implements DbKodeId2<T> {

    protected DbKodeIdImpl2(Long value, ReplicaVersion2 replicaVersion) {
        super(value, replicaVersion);
    }
}
