package no.statkart.skif.store.kodelistesupport;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public interface EnumKodeId<T extends EnumKode> extends KodeId<T> {
    @Override
    Long getValue();
}
