package no.statkart.skif.store.kodelistesupport;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class EnumKode extends KodeImpl  {
    private String beskrivelsesKey;

    @Override
    public EnumKodeId<?> getId() {
        return (EnumKodeId<?>) super.getId();
    }

    public String getBeskrivelsesKey() {
        return beskrivelsesKey;
    }

    public void setBeskrivelsesKey(String beskrivelsesKey) {
        this.beskrivelsesKey = beskrivelsesKey;
    }
}
