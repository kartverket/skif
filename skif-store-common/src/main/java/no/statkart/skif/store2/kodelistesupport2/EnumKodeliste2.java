package no.statkart.skif.store2.kodelistesupport2;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public interface EnumKodeliste2 extends Kodeliste2 {
    public EnumKodelisteId2<?> getId();

    public String getBeskrivelsesKey();

    public void setBeskrivelsesKey(String beskrivelsesKey);
}
