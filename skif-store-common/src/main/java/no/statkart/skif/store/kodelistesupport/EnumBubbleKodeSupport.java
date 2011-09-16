package no.statkart.skif.store.kodelistesupport;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.ReplicaVersion;

import java.util.*;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class EnumBubbleKodeSupport<KL extends EnumBubbleKodeliste, KLId extends EnumBubbleKodelisteId<? extends KL>> extends BubbleKodeSupport<KLId> {
    private Map<BubbleKodeId<?>, EnumBubbleKode> koder = new HashMap<BubbleKodeId<?>, EnumBubbleKode>();
    private KL nonLocalizedKodeliste;

    public static <I extends EnumBubbleKodeId<?>> EnumBubbleKodeSupport getKodeSupport(Class<I> idClass) {
        return (EnumBubbleKodeSupport) BubbleKodeSupport.getKodeSupport(idClass);
    }

    public EnumBubbleKodeSupport(Class<? extends EnumBubbleKodeId<?>> idClass, KLId kodelisteId , String kodelisteNavn) {
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

    public synchronized void addKode(EnumBubbleKode kode) {
        if (koder.containsKey(kode.getId())) {
            throw new ImplementationException("Forsøk på å definere samme kode flere ganger: " + kode );
        }
        koder.put(kode.getId(), kode);
    }

    public Collection<EnumBubbleKode> getNonLocalizedKoder() {
        return koder.values();
    }

    public <T extends EnumBubbleKode, I extends EnumBubbleKodeId<? extends T>> I define(Class<I> idClass, long idValue, String kodeverdi, String beskrivelesesKey) {
        I id = BubbleId.createInstance(idClass, idValue, ReplicaVersion.CURRENT);
        T kode = id.createTypeInstance();
        kode.setId(id);
        kode.setKodeverdi(kodeverdi);
        kode.setBeskrivelsesKey(beskrivelesesKey);
        addKode(kode);
        return id;
    }

    @Override
    protected <T extends BubbleKode> String getBeskrivelse(T kode, Locale locale) {
        // TODO: implementer uthenting fra resourse fil
        return ((EnumBubbleKode)kode).getBeskrivelsesKey() + " lokalister for " + locale;
    }

    @Override
    protected <T extends BubbleKodeliste> String getBeskrivelse(T kodeliste, Locale locale) {
        // TODO: implementer uthenting fra resourse fil
        return ((EnumBubbleKode)kodeliste).getBeskrivelsesKey() + " lokalister for " + locale;
    }
}
