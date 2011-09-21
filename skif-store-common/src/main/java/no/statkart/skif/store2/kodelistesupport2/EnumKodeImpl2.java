package no.statkart.skif.store2.kodelistesupport2;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class EnumKodeImpl2 extends KodeImpl2 implements BubbleEnumKode2 {
    private String beskrivelsesKey;

    @Override
    public EnumKodeIdImpl2<?> getId() {
        return (EnumKodeIdImpl2<?>) super.getId();
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
