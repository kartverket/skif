package no.statkart.skif.store2.kodelistesupport2;


/**
 * Dette er bare en hjelpeklasse for test, tror jeg
 * @author Henrik Fredholm
 * @since 2.0
 */
public final class EnumKodeSupport2 extends BubbleEnumKodeSupport2<EnumKodeliste2, EnumKodelisteId2<EnumKodeliste2>> {
    public EnumKodeSupport2(Class<? extends EnumKodeIdImpl2<?>> idClass, long kodelisteIdValue, String kodelisteNavn) {
        super(idClass, new EnumKodelisteIdImpl2(kodelisteIdValue), kodelisteNavn);
    }
}
