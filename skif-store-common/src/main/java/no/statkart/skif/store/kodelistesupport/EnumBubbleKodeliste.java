package no.statkart.skif.store.kodelistesupport;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public interface EnumBubbleKodeliste extends BubbleKodeliste {
    public EnumBubbleKodelisteId<?> getId();

    public String getBeskrivelsesKey();

    public void setBeskrivelsesKey(String beskrivelsesKey);
}
