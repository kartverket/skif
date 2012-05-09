package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Knut Inge Bøe
 */
public class NyeVilkaarIFesteavtaleId<T extends NyeVilkaarIFesteavtale> extends PaategningForMatrikkelenheterId<T> {
    public NyeVilkaarIFesteavtaleId(Long value) {
        super(value);
    }

    public NyeVilkaarIFesteavtaleId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static NyeVilkaarIFesteavtaleId<?> create(long value) {
        return new NyeVilkaarIFesteavtaleId<NyeVilkaarIFesteavtale>(new Long(value));
    }
}
