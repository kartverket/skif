package no.statkart.skif.store.kodelistesupport;


import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;

import java.util.Locale;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class DbKodeSupport<KL extends DbKodeliste, KLID extends DbKodelisteId<KL>> extends KodeSupport<KL, KLID> {

    public DbKodeSupport(Class<? extends KodeId<?>> idClass, KLID kodelisteId) {
        super(idClass, kodelisteId);
    }

    public <I extends DbKodeImplId<? extends DbKodeImpl>> I define(Class<I> idClass, long idValue) {
        I id = BubbleIds.createInstance(idClass, idValue, SnapshotVersion.CURRENT);
        return id;
    }


    @Override
    protected <T extends Kode> String getBeskrivelse(T kode, Locale locale) {
        // TODO: Bruk lokale
        return ((DbKodeImpl)kode).getLokalisertBeskrivelse().get("b");
    }

    @Override
    protected <T extends Kodeliste> String getBeskrivelse(T kodeliste, Locale locale) {
        // TODO: Bruk lokale
        return ((DbKodeliste)kodeliste).getLokalisertBeskrivelse().get("b");
    }

}
