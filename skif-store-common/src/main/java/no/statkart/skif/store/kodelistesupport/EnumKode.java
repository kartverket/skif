package no.statkart.skif.store.kodelistesupport;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public interface EnumKode extends Kode {
    public EnumKodeId<?> getId();
    public String getBeskrivelsesKey();
    public void setBeskrivelsesKey(String beskrivelsesKey);
}
