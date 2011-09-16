package no.statkart.skif.store.kodelistesupport;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.ReplicaVersion;
import java.util.Locale;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class DbBubbleKodeSupport<KL extends DbBubbleKodeliste, KLId extends DbBubbleKodelisteId<? extends KL>> extends BubbleKodeSupport<KLId> {

    public static <I extends DbBubbleKodeId<?>> DbBubbleKodeSupport getKodeSupport(Class<I> idClass) {
        return (DbBubbleKodeSupport) BubbleKodeSupport.getKodeSupport(idClass);
    }

    public DbBubbleKodeSupport(Class<? extends DbBubbleKodeId<?>> idClass, KLId kodelisteId) {
        super(idClass, kodelisteId);
    }

    public <I extends DbBubbleKodeId<? extends DbBubbleKode>> I define(Class<I> idClass, long idValue) {
        I id = BubbleId.createInstance(idClass, idValue, ReplicaVersion.CURRENT);
        return id;
    }

    @Override
    protected <T extends BubbleKode> String getBeskrivelse(T kode, Locale locale) {
        // TODO: Bruk lokale
        return ((DbBubbleKode)kode).getLokalisertBeskrivelse().get("b");
    }

    @Override
    protected <T extends BubbleKodeliste> String getBeskrivelse(T kodeliste, Locale locale) {
        // TODO: Bruk lokale
        return ((DbBubbleKodeliste)kodeliste).getLokalisertBeskrivelse().get("b");
    }
}
