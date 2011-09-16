package no.statkart.skif.storetest.domain.kodeliste.impl;

import no.statkart.skif.store.kodelistesupport.EnumBubbleKodeliste;
import no.statkart.skif.storetest.domain.kodeliste.Kodeliste;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class EnumKodeliste extends Kodeliste implements EnumBubbleKodeliste {
    private String beskrivelsesKey;

    @Override
    public EnumKodelisteId getId() {
        return (EnumKodelisteId) super.getId();
    }

    public String getBeskrivelsesKey() {
        return beskrivelsesKey;
    }

    public void setBeskrivelsesKey(String beskrivelsesKey) {
        this.beskrivelsesKey = beskrivelsesKey;
    }
}
