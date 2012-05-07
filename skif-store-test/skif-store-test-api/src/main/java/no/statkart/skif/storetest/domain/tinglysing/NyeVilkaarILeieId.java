package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Knut Inge Bøe
 */
public class NyeVilkaarILeieId <T extends NyeVilkaarILeie> extends RettsstiftelseId<T>{
    public NyeVilkaarILeieId(Long value) {
        super(value);
    }

    public NyeVilkaarILeieId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
