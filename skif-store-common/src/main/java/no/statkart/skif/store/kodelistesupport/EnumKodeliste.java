package no.statkart.skif.store.kodelistesupport;

import java.util.List;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public interface EnumKodeliste extends Kodeliste {
    public EnumKodelisteId<?> getId();

    public String getBeskrivelsesKey();

    public void setBeskrivelsesKey(String beskrivelsesKey);

    @Override
    List<KodeId<?>> getKodeIds();
}
