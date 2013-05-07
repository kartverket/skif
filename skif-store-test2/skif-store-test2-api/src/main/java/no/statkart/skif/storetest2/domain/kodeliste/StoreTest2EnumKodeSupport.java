package no.statkart.skif.storetest2.domain.kodeliste;

import no.statkart.skif.store.kodeliste.EnumKodeSupport;

/**
 * @param <T>    kodeklassen
 * @param <I>    kode-id-klassen
 * @author Tor Egil R. Strand
 * @since 2.3.0
 */
public class StoreTest2EnumKodeSupport<T extends StoreTest2Kode, I extends StoreTest2KodeId<T>> extends EnumKodeSupport<T, I, StoreTest2KodelisteLong, StoreTest2KodelisteLongId<StoreTest2KodelisteLong>> {
    public StoreTest2EnumKodeSupport(Class<I> kodeIdClass, long kodelisteIdValue, String resourceName) {
        super(kodeIdClass, new StoreTest2KodelisteLongId<StoreTest2KodelisteLong>(kodelisteIdValue), resourceName);
    }
}