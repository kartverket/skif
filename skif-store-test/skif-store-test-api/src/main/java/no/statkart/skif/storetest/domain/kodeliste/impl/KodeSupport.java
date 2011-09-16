package no.statkart.skif.storetest.domain.kodeliste.impl;

import no.statkart.skif.store.kodelistesupport.*;
import no.statkart.skif.storetest.domain.kodeliste.KodeId;
import no.statkart.skif.storetest.domain.kodeliste.KodelisteId;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public abstract class KodeSupport extends BubbleKodeSupport<KodelisteId<?>> {
    public KodeSupport(Class<? extends KodeId<?>> idClass, KodelisteId kodelisteId) {
        super(idClass, kodelisteId);
    }
}
