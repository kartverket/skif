package no.statkart.skif.store.kodelistesupport;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;

import java.util.*;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class EnumKodeSupport<KL extends EnumKodeliste, KLID extends EnumKodelisteId<KL>> extends KodeSupport<KL, KLID> {
    private Map<KodeId<?>, EnumKodeImpl> koder = new HashMap<KodeId<?>, EnumKodeImpl>();
    private KL nonLocalizedKodeliste;

    public static <I extends EnumKodeImplId<?>> EnumKodeSupport getKodeSupport(Class<I> idClass) {
        return (EnumKodeSupport) KodeSupport.getKodeSupport(idClass);
    }

    public KL getNonLocalizedKodeliste() {
        nonLocalizedKodeliste.setKodeIds(new ArrayList(koder.keySet()));
        return nonLocalizedKodeliste;
    }



    public synchronized void addKode(EnumKodeImpl kode) {
        if (koder.containsKey(kode.getId())) {
            throw new ImplementationException("Forsøk på å definere samme kode flere ganger: " + kode );
        }
        koder.put(kode.getId(), kode);
    }

    public EnumKodeSupport(Class<? extends EnumKodeImplId<?>> idClass, KLID kodelisteId, String kodelisteNavn) {
        super(idClass, kodelisteId);
        nonLocalizedKodeliste =  kodelisteId.createTypeInstance();
        nonLocalizedKodeliste.setId(getKodelisteId());
        nonLocalizedKodeliste.setKodeIdClass(idClass);
        nonLocalizedKodeliste.setNavn(kodelisteNavn);
        nonLocalizedKodeliste.setBeskrivelsesKey(kodelisteNavn);
    }

    public <T extends EnumKodeImpl, I extends EnumKodeImplId<? extends T>> T defineKode(Class<I> idClass, long idValue, String kodeverdi, String beskrivelesesKey) {
        I id = BubbleIds.createInstance(idClass, idValue, SnapshotVersion.CURRENT);
        T kode = id.createTypeInstance();
        kode.setId(id);
        kode.setKodeverdi(kodeverdi);
        kode.setBeskrivelsesKey(beskrivelesesKey);
        addKode(kode);
        return kode;
    }

    public Collection<EnumKodeImpl> getNonLocalizedKoder() {
        return koder.values();
    }

    @Override
    protected <T extends Kode> String getBeskrivelse(T kode, Locale locale) {
        // TODO: implementer uthenting fra resourse fil
        return ((EnumKodeImpl)kode).getBeskrivelsesKey() + " lokalister for " + locale;
    }

    @Override
    protected <T extends Kodeliste> String getBeskrivelse(T kodeliste, Locale locale) {
        // TODO: implementer uthenting fra resourse fil
        return ((EnumKodeliste)kodeliste).getBeskrivelsesKey() + " lokalister for " + locale;
    }
}
