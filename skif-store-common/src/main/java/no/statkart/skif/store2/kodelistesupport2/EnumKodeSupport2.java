package no.statkart.skif.store2.kodelistesupport2;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.ReplicaVersion;
import no.statkart.skif.store2.BubbleIds2;

import java.util.*;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class EnumKodeSupport2<KL extends EnumKodeliste2, KLID extends EnumKodelisteId2<KL>> extends KodeSupport2<KL, KLID> {
    private Map<KodeId2<?>, EnumKode2> koder = new HashMap<KodeId2<?>, EnumKode2>();
    private KL nonLocalizedKodeliste;

    public static <I extends EnumKodeId2<?>> EnumKodeSupport2 getKodeSupport(Class<I> idClass) {
        return (EnumKodeSupport2) KodeSupport2.getKodeSupport(idClass);
    }

    public EnumKodeSupport2(Class<? extends EnumKodeId2<?>> idClass, KLID kodelisteId, String kodelisteNavn) {
        super(idClass, kodelisteId);
        nonLocalizedKodeliste =  kodelisteId.createTypeInstance();
        nonLocalizedKodeliste.setId(getKodelisteId());
        nonLocalizedKodeliste.setKodeIdClass(idClass);
        nonLocalizedKodeliste.setNavn(kodelisteNavn);
        nonLocalizedKodeliste.setBeskrivelsesKey(kodelisteNavn);
    }



    public KL getNonLocalizedKodeliste() {
        nonLocalizedKodeliste.setKodeIds(new ArrayList(koder.keySet()));
        return nonLocalizedKodeliste;
    }

    public synchronized void addKode(EnumKode2 kode) {
        if (koder.containsKey(kode.getId())) {
            throw new ImplementationException("Forsøk på å definere samme kode flere ganger: " + kode );
        }
        koder.put(kode.getId(), kode);
    }

    public Collection<EnumKode2> getNonLocalizedKoder() {
        return koder.values();
    }

    public <T extends EnumKode2, I extends EnumKodeId2<? extends T>> I define(Class<I> idClass, long idValue, String kodeverdi, String beskrivelesesKey) {
        I id = BubbleIds2.createInstance(idClass, idValue, ReplicaVersion.CURRENT);
        T kode = id.createTypeInstance();
        kode.setId(id);
        kode.setKodeverdi(kodeverdi);
        kode.setBeskrivelsesKey(beskrivelesesKey);
        addKode(kode);
        return id;
    }

    @Override
    protected <T extends Kode2> String getBeskrivelse(T kode, Locale locale) {
        // TODO: implementer uthenting fra resourse fil
        return ((EnumKode2)kode).getBeskrivelsesKey() + " lokalister for " + locale;
    }

    @Override
    protected <T extends Kodeliste2> String getBeskrivelse(T kodeliste, Locale locale) {
        // TODO: implementer uthenting fra resourse fil
        return ((EnumKode2)kodeliste).getBeskrivelsesKey() + " lokalister for " + locale;
    }
}
