package no.statkart.skif.storetest.domain.tinglysing;


import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Knut Inge Bøe
 */
public class NyeVilkaarIFesteavtaleId <T extends NyeVilkaarIFesteavtale> extends RettsstiftelseId<T>{
    public NyeVilkaarIFesteavtaleId(Long value) {
        super(value);
    }

    public NyeVilkaarIFesteavtaleId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
