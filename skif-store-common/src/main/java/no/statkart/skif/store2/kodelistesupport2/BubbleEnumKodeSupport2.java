package no.statkart.skif.store2.kodelistesupport2;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.ReplicaVersion;
import no.statkart.skif.store2.BubbleIds2;

import java.util.*;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class BubbleEnumKodeSupport2<KL extends EnumKodeliste2, KLID extends EnumKodelisteId2<KL>> extends BubbleKodeSupport2<KL, KLID> {
    private Map<BubbleKodeId2<?>, BubbleEnumKode2> koder = new HashMap<BubbleKodeId2<?>, BubbleEnumKode2>();
    private KL nonLocalizedKodeliste;

    public static <I extends BubbleEnumKodeId2<?>> BubbleEnumKodeSupport2 getKodeSupport(Class<I> idClass) {
        return (BubbleEnumKodeSupport2) BubbleKodeSupport2.getKodeSupport(idClass);
    }

    public BubbleEnumKodeSupport2(Class<? extends BubbleEnumKodeId2<?>> idClass, KLID kodelisteId, String kodelisteNavn) {
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

    public synchronized void addKode(BubbleEnumKode2 kode) {
        if (koder.containsKey(kode.getId())) {
            throw new ImplementationException("Forsøk på å definere samme kode flere ganger: " + kode );
        }
        koder.put(kode.getId(), kode);
    }

    public Collection<BubbleEnumKode2> getNonLocalizedKoder() {
        return koder.values();
    }

    public <T extends BubbleEnumKode2, I extends BubbleEnumKodeId2<? extends T>> I define(Class<I> idClass, long idValue, String kodeverdi, String beskrivelesesKey) {
        I id = BubbleIds2.createInstance(idClass, idValue, ReplicaVersion.CURRENT);
        T kode = id.createTypeInstance();
        kode.setId(id);
        kode.setKodeverdi(kodeverdi);
        kode.setBeskrivelsesKey(beskrivelesesKey);
        addKode(kode);
        return id;
    }

    @Override
    protected <T extends BubbleKode2> String getBeskrivelse(T kode, Locale locale) {
        // TODO: implementer uthenting fra resourse fil
        return ((BubbleEnumKode2)kode).getBeskrivelsesKey() + " lokalister for " + locale;
    }

    @Override
    protected <T extends Kodeliste2> String getBeskrivelse(T kodeliste, Locale locale) {
        // TODO: implementer uthenting fra resourse fil
        return ((BubbleEnumKode2)kodeliste).getBeskrivelsesKey() + " lokalister for " + locale;
    }
}
