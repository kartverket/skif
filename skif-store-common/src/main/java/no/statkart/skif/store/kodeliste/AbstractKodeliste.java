package no.statkart.skif.store.kodeliste;

import no.statkart.skif.SkifUtil;
import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.internal.util.InternalLocaleUtils;

import java.util.*;

/**
 * Abstrakt implementasjon av kodeliste. Klasse holder på en liste av {@code KodeId}s og implementere
 * lokaliseringsstøtte.
 * <p/>
 * Klassen er knyttet mot {@link AbstractKodelisteId} som bruker {@code Object} som idValue type. Det finnes
 * konkrete subtyper som bruker {@code Long} og {@code String} som idValue type.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public abstract class AbstractKodeliste extends AbstractBubbleObject implements Kodeliste {
    private static final long serialVersionUID = 1L;

    private String kodeTypeNavn;
    private Class<? extends KodeId<?>> kodeIdClass;
    private List<KodeId<?>> kodeIds = new ArrayList<KodeId<?>>();
    private boolean editerbar;

    // Avledet felt
    private Class<? extends Kode> kodeClass;

    public String getKodeTypeNavn() {
        return kodeTypeNavn;
    }

    public void setKodeTypeNavn(String kodeTypeNavn) {
        this.kodeTypeNavn = kodeTypeNavn;
    }

    @Override
    public AbstractKodelisteId<?> getId() {
        return (AbstractKodelisteId<?>) super.getId();
    }

    @Override
    public Class<? extends Kode> getKodeClass() {
        if (kodeClass==null) {
            String kodeIdClassName = kodeIdClass.getName();
            String kodeClassName = kodeIdClassName.substring(0, kodeIdClassName.length()-2);
            kodeClass = SkifUtil.classForName(kodeClassName);
        }
        return kodeClass;
    }

    @Override
    public Class<? extends KodeId<?>> getKodeIdClass() {
        return kodeIdClass;
    }

    @Override
    public void setKodeIdClass(Class<? extends KodeId<?>> kodeIdClass) {
        this.kodeIdClass = kodeIdClass;
    }

    @Override
    public List<KodeId<?>> getKodeIds() {
        return kodeIds;
    }

    @Override
    public void setKodeIds(List<? extends KodeId<?>> kodeIds) {
        this.kodeIds = (List) kodeIds;
    }

    public List<Kode> getKoder() {
        return store.get(kodeIds);
    }


    @Override
    public boolean isEditerbar() {
        return editerbar;
    }

    @Override
    public void setEditerbar(boolean editerbar) {
        this.editerbar = editerbar;
    }
}

