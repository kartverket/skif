package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.store.SnapshotVersion;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;

/**
 * @author Roar Ingebrigtsen
 * @since 2.5
 */
public class SnapshotVersionTypeMapper extends AbstractStoreTestTypeMapper<no.statkart.skif.storetest.wsapi.domain.SnapshotVersion, SnapshotVersion> {

    protected SnapshotVersionTypeMapper() {
        super(no.statkart.skif.storetest.wsapi.domain.SnapshotVersion.class, SnapshotVersion.class);
    }

    @Override
    public void mapDomainObject(SnapshotVersion source, no.statkart.skif.storetest.wsapi.domain.SnapshotVersion target) {
        super.mapDomainObject(source, target);

        Timestamp timestamp = source.getTimestamp();
        target.setTime(map.d2w(timestamp.getTime()));
        target.setNanos(map.d2w(timestamp.getNanos()));
    }

    @Override
    public void mapWsapiObject(no.statkart.skif.storetest.wsapi.domain.SnapshotVersion source, SnapshotVersion target) {
        super.mapWsapiObject(source, target);
    }

    @Override
    protected SnapshotVersion getInitialDomainObject(no.statkart.skif.storetest.wsapi.domain.SnapshotVersion source) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Timestamp timestamp = new Timestamp(source.getTime());
        timestamp.setNanos(source.getNanos());
        return SnapshotVersion.createInstance(timestamp);
    }
}
