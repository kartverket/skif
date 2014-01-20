package no.statkart.skif.store.kodeliste;

import no.statkart.skif.store.BubbleObject;

import java.util.List;

/**
 * Interface for Kodeliste
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface Kodeliste extends BubbleObject {
    @Override
    KodelisteId<?> getId();

    Class<? extends Kode> getKodeClass();

    Class<? extends KodeId<?>> getKodeIdClass();

    void setKodeIdClass(Class<? extends KodeId<?>> kodeIdClass);

    List<KodeId<?>> getKoderIds();

    void setKoderIds(List<? extends KodeId<?>> kodeIds);


    boolean isEditerbar();

    void setEditerbar(boolean editerbar);
}
