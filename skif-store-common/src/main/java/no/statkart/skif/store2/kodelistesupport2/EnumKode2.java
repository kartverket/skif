package no.statkart.skif.store2.kodelistesupport2;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public interface EnumKode2 extends Kode2 {
    public EnumKodeId2<?> getId();
    public String getBeskrivelsesKey();
    public void setBeskrivelsesKey(String beskrivelsesKey);
}
