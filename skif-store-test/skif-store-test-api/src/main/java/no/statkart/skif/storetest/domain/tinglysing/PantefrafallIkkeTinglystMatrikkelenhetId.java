package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Knut Inge Bøe
 */
public class PantefrafallIkkeTinglystMatrikkelenhetId<T extends PantefrafallIkkeTinglystMatrikkelenhet> extends PaategningPaaRettsstiftelserId<T> {
    public PantefrafallIkkeTinglystMatrikkelenhetId(Long value) {
        super(value);
    }

    public PantefrafallIkkeTinglystMatrikkelenhetId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static PantefrafallIkkeTinglystMatrikkelenhetId<?> create(long value) {
        return new PantefrafallIkkeTinglystMatrikkelenhetId<PantefrafallIkkeTinglystMatrikkelenhet>(new Long(value));
    }
}
