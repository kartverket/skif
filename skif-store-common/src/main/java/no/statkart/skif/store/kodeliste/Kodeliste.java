package no.statkart.skif.store.kodeliste;

import no.statkart.skif.store.BubbleId;
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

    List<KodeId<?>> getKodeIds();

    void setKodeIds(List<? extends KodeId<?>> kodeIds);


    boolean isEditerbar();

    void setEditerbar(boolean editerbar);

    void localize(String localeString);

    void updateLocalized(String localeString);

    String getNavn();

    void setNavn(String s);


    String getBeskrivelse();

    void setBeskrivelse(String s);


}
