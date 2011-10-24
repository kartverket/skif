package no.statkart.skif.store.kodelistesupport;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class EnumKodeImpl extends KodeImpl implements EnumKode {
    private String beskrivelsesKey;

    @Override
    public EnumKodeIdImpl<?> getId() {
        return (EnumKodeIdImpl<?>) super.getId();
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
