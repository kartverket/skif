package no.statkart.skif.store.kodelistesupport;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public interface EnumBubbleKode extends BubbleKode {
    public EnumBubbleKodeId<?> getId();
    public String getBeskrivelsesKey();
    public void setBeskrivelsesKey(String beskrivelsesKey);
}
