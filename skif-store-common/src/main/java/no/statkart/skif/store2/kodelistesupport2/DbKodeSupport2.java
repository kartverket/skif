package no.statkart.skif.store2.kodelistesupport2;


import no.statkart.skif.store2.BubbleId2;
import no.statkart.skif.store2.BubbleIds2;
import no.statkart.skif.store2.ReplicaVersion2;

import java.util.Locale;

/**
 * @author Roar Ingebrigtsen
 * @since 2.5
 */
public class DbKodeSupport2<KL extends DbKodeliste2, KLID extends DbKodelisteId2<KL>> extends KodeSupport2<KL, KLID> {

    public DbKodeSupport2(Class<? extends KodeId2<?>> idClass, KLID kodelisteId) {
        super(idClass, kodelisteId);
    }

    public <I extends DbKodeId2<? extends DbKode2>> I define(Class<I> idClass, long idValue) {
        I id = BubbleIds2.createInstance(idClass, idValue, ReplicaVersion2.CURRENT);
        return id;
    }


    @Override
    protected <T extends Kode2> String getBeskrivelse(T kode, Locale locale) {
        // TODO: Bruk lokale
        return ((DbKode2)kode).getLokalisertBeskrivelse().get("b");
    }

    @Override
    protected <T extends Kodeliste2> String getBeskrivelse(T kodeliste, Locale locale) {
        // TODO: Bruk lokale
        return ((DbKodeliste2)kodeliste).getLokalisertBeskrivelse().get("b");
    }

}
