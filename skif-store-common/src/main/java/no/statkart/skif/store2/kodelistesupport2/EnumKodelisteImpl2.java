package no.statkart.skif.store2.kodelistesupport2;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class EnumKodelisteImpl2 extends KodelisteImpl2 implements EnumKodeliste2 {
    private String beskrivelsesKey;

    @Override
    public EnumKodelisteIdImpl2 getId() {
        return (EnumKodelisteIdImpl2) super.getId();
    }

    public String getBeskrivelsesKey() {
        return beskrivelsesKey;
    }

    public void setBeskrivelsesKey(String beskrivelsesKey) {
        this.beskrivelsesKey = beskrivelsesKey;
    }
}
