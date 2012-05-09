package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Knut Inge Bøe
 */
public class NyeVilkaarILeieId<T extends NyeVilkaarILeie> extends PaategningForMatrikkelenheterId<T> {
    public NyeVilkaarILeieId(Long value) {
        super(value);
    }

    public NyeVilkaarILeieId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static NyeVilkaarILeieId<?> create(long value) {
        return new NyeVilkaarILeieId<NyeVilkaarILeie>(new Long(value));
    }
}
