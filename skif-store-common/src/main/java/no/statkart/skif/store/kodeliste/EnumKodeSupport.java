package no.statkart.skif.store.kodeliste;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;

import java.util.*;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class EnumKodeSupport<KL extends EnumKodeliste, KLID extends EnumKodelisteId<KL>> extends KodeSupport<KL, KLID> {
    private Map<KodeId<?>, EnumKode> koder = new HashMap<KodeId<?>, EnumKode>();
    private KL nonLocalizedKodeliste;
    private final KodeIdResolver kodeIdResolver = new KodeIdResolver();

    public static <I extends EnumKodeId<?>> EnumKodeSupport getKodeSupport(Class<I> idClass) {
        return (EnumKodeSupport) KodeSupport.getKodeSupport(idClass);
    }

    public <I extends KodeId<? extends Kode>> I getOrCreate(I kodeId) {
        return kodeIdResolver.getOrCreate(kodeId);
    }

    public boolean isNewKoderAllowed() {
        return kodeIdResolver.isNewKoderAllowed();
    }

    public void setNewKoderAllowed(boolean value) {
        kodeIdResolver.setNewKoderAllowed(value);
    }

    public KL getNonLocalizedKodeliste() {
        nonLocalizedKodeliste.setKodeIds(new ArrayList(koder.keySet()));
        return nonLocalizedKodeliste;
    }

    public synchronized void addKode(EnumKode kode) {
        if (koder.containsKey(kode.getId())) {
            throw new ImplementationException("Forsøk på å definere samme kode flere ganger: " + kode );
        }
        koder.put(kode.getId(), kode);
    }

    public EnumKodeSupport(Class<? extends EnumKodeId<?>> idClass, KLID kodelisteId, String kodelisteNavn) {
        super(idClass, kodelisteId);
        nonLocalizedKodeliste =  kodelisteId.createTypeInstance();
        nonLocalizedKodeliste.setId(getKodelisteId());
        nonLocalizedKodeliste.setKodeIdClass(idClass);
        nonLocalizedKodeliste.setNavn(kodelisteNavn);
        nonLocalizedKodeliste.setBeskrivelsesKey(kodelisteNavn);
    }

    public <T extends EnumKode, I extends EnumKodeId<? extends T>> T defineKode(Class<I> idClass, long idValue, String kodeverdi, String beskrivelesesKey) {
        I id = BubbleIds.createInstance(idClass, new Long(idValue), SnapshotVersion.CURRENT);
        T kode = id.createTypeInstance();
        kode.setId(id);
        kode.setKodeverdi(kodeverdi);
        kode.setBeskrivelsesKey(beskrivelesesKey);
        addKode(kode);
        return kode;
    }

    public Collection<EnumKode> getNonLocalizedKoder() {
        return koder.values();
    }

    @Override
    protected <T extends Kode> String getBeskrivelse(T kode, Locale locale) {
        // TODO: implementer uthenting fra resourse fil
        return ((EnumKode)kode).getBeskrivelsesKey() + " lokalister for " + locale;
    }

    @Override
    protected <T extends Kodeliste> String getBeskrivelse(T kodeliste, Locale locale) {
        // TODO: implementer uthenting fra resourse fil
        return ((EnumKodeliste)kodeliste).getBeskrivelsesKey() + " lokalister for " + locale;
    }
}
