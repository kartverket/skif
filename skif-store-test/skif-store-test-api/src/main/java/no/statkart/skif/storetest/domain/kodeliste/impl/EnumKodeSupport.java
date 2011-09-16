package no.statkart.skif.storetest.domain.kodeliste.impl;

import no.statkart.skif.store.kodelistesupport.EnumBubbleKodeSupport;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class EnumKodeSupport extends EnumBubbleKodeSupport<EnumKodeliste, EnumKodelisteId> {
    public EnumKodeSupport(Class<? extends EnumKodeId<?>> idClass, long kodelisteIdValue, String kodelisteNavn) {
        super(idClass, new EnumKodelisteId(kodelisteIdValue), kodelisteNavn);
    }
}
