package no.statkart.skif.store2.kodelistesupport2;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class EnumKode2 extends Kode2 implements BubbleEnumKode2 {
    private String beskrivelsesKey;

    @Override
    public EnumKodeId2<?> getId() {
        return (EnumKodeId2<?>) super.getId();
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
