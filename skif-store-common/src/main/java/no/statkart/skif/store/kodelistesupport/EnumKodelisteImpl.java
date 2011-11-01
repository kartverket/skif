package no.statkart.skif.store.kodelistesupport;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class EnumKodelisteImpl extends KodelisteImpl implements EnumKodeliste {
    private String beskrivelsesKey;

    @Override
    public EnumKodelisteImplId getId() {
        return (EnumKodelisteImplId) super.getId();
    }

    public String getBeskrivelsesKey() {
        return beskrivelsesKey;
    }

    public void setBeskrivelsesKey(String beskrivelsesKey) {
        this.beskrivelsesKey = beskrivelsesKey;
    }
}
