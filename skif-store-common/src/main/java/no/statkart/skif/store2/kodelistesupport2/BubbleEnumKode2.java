package no.statkart.skif.store2.kodelistesupport2;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public interface BubbleEnumKode2 extends BubbleKode2 {
    public BubbleEnumKodeId2<?> getId();
    public String getBeskrivelsesKey();
    public void setBeskrivelsesKey(String beskrivelsesKey);
}
