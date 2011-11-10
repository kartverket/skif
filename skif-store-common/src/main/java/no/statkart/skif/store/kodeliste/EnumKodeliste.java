package no.statkart.skif.store.kodeliste;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class EnumKodeliste extends Kodeliste {
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
