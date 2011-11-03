package no.statkart.skif.store.kodelistesupport;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class EnumKodeImpl extends KodeImpl  {
    private String beskrivelsesKey;

    @Override
    public EnumKodeImplId<?> getId() {
        return (EnumKodeImplId<?>) super.getId();
    }

    public String getBeskrivelsesKey() {
        return beskrivelsesKey;
    }

    public void setBeskrivelsesKey(String beskrivelsesKey) {
        this.beskrivelsesKey = beskrivelsesKey;
    }
}
