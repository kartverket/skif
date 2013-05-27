package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.store.SnapshotVersion;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;

/**
 * @author Roar Ingebrigtsen
 * @author Tor Egil R. Strand
 * @since 2.0
 */
public class SnapshotVersionTypeMapper extends AbstractStoreTestTypeMapper<no.statkart.skif.storetest.wsapi.domain.SnapshotVersion, SnapshotVersion> {

    protected SnapshotVersionTypeMapper() {
        super(no.statkart.skif.storetest.wsapi.domain.SnapshotVersion.class, SnapshotVersion.class);
    }

    @Override
    public no.statkart.skif.storetest.wsapi.domain.SnapshotVersion mapDomainObject(SnapshotVersion source) {
        Timestamp timestamp = source.getTimestamp();
        no.statkart.skif.storetest.wsapi.domain.SnapshotVersion target = new no.statkart.skif.storetest.wsapi.domain.SnapshotVersion();
        target.setTime(getMapping().d2w(timestamp.getTime()));
        target.setNanos(getMapping().d2w(timestamp.getNanos()));
        return target;
    }

    @Override
    public SnapshotVersion mapWsapiObject(no.statkart.skif.storetest.wsapi.domain.SnapshotVersion source) {
        Timestamp timestamp = new Timestamp(source.getTime());
        timestamp.setNanos(source.getNanos());
        return SnapshotVersion.createInstance(timestamp);
    }
}
