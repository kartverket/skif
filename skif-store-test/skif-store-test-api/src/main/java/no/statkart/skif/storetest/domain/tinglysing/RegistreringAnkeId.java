package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Knut Inge Bøe
 */
public class RegistreringAnkeId<T extends RegistreringAnke> extends PaategningPaaRettsstiftelserId<T> {
    public RegistreringAnkeId(Long value) {
        super(value);
    }

    public RegistreringAnkeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static RegistreringAnkeId<?> create(long value) {
        return new RegistreringAnkeId<RegistreringAnke>(new Long(value));
    }
}
