package no.statkart.skif.store.kodelistesupport;


import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;

import java.util.Locale;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class DbKodeSupport<KL extends DbKodelisteImpl, KLID extends DbKodelisteImplId<KL>> extends KodeSupport<KL, KLID> {

    public DbKodeSupport(Class<? extends KodeId<?>> idClass, KLID kodelisteId) {
        super(idClass, kodelisteId);
    }

    public <I extends DbKodeId<? extends DbKode>> I define(Class<I> idClass, long idValue) {
        I id = BubbleIds.createInstance(idClass, idValue, SnapshotVersion.CURRENT);
        return id;
    }


    @Override
    protected <T extends Kode> String getBeskrivelse(T kode, Locale locale) {
        // TODO: Bruk lokale
        return ((DbKode)kode).getLokalisertBeskrivelse().get("b");
    }

    @Override
    protected <T extends KodelisteImpl> String getBeskrivelse(T kodeliste, Locale locale) {
        // TODO: Bruk lokale
        return ((DbKodelisteImpl)kodeliste).getLokalisertBeskrivelse().get("b");
    }

}
