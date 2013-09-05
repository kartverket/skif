package no.statkart.skif.wsversioning.wsapi.v1.mapping;

import no.statkart.skif.store.SnapshotVersion;

import java.sql.Timestamp;

/**
 * @author Roar Ingebrigtsen
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class SnapshotVersionTypeMapper extends AbstractWSVersioningTypeMapper<no.statkart.skif.wsversioning.wsapi.v1.domain.SnapshotVersion, SnapshotVersion> {

    protected SnapshotVersionTypeMapper() {
        super(no.statkart.skif.wsversioning.wsapi.v1.domain.SnapshotVersion.class, SnapshotVersion.class);
    }

    @Override
    public no.statkart.skif.wsversioning.wsapi.v1.domain.SnapshotVersion mapDomainObject(SnapshotVersion source) {
        Timestamp timestamp = source.getTimestamp();
        no.statkart.skif.wsversioning.wsapi.v1.domain.SnapshotVersion target = new no.statkart.skif.wsversioning.wsapi.v1.domain.SnapshotVersion();
        target.setTime(getMapping().d2w(timestamp.getTime()));
        target.setNanos(getMapping().d2w(timestamp.getNanos()));
        return target;
    }

    @Override
    public SnapshotVersion mapWsapiObject(no.statkart.skif.wsversioning.wsapi.v1.domain.SnapshotVersion source) {
        Timestamp timestamp = new Timestamp(source.getTime());
        timestamp.setNanos(source.getNanos());
        return SnapshotVersion.createInstance(timestamp);
    }
}