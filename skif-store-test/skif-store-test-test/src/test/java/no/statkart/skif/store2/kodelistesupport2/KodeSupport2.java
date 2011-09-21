package no.statkart.skif.store2.kodelistesupport2;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class KodeSupport2 extends BubbleKodeSupport2<Kodeliste2, KodelisteId2<Kodeliste2>> {
    public KodeSupport2(Class<? extends KodeId2<?>> idClass, KodelisteIdImpl2 kodelisteId) {
        super(idClass, kodelisteId);
    }
}
