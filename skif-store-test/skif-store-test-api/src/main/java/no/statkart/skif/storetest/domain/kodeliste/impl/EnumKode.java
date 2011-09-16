package no.statkart.skif.storetest.domain.kodeliste.impl;

import no.statkart.skif.store.kodelistesupport.EnumBubbleKode;
import no.statkart.skif.storetest.domain.kodeliste.Kode;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public abstract class EnumKode extends Kode implements EnumBubbleKode {
    private String beskrivelsesKey;

    @Override
    public EnumKodeId<?> getId() {
        return (EnumKodeId<?>) super.getId();
    }

    @Override
    public String getBeskrivelsesKey() {
        return beskrivelsesKey;
    }

    @Override
    public void setBeskrivelsesKey(String beskrivelsesKey) {
        this.beskrivelsesKey = beskrivelsesKey;
    }
}
