package no.statkart.skif.storetest.domain.endringslogg;

import com.google.common.collect.Lists;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.endringslogg.AbstractEndringId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Et objekt som inneholder resultatet av et endringslogg kall ({@link no.statkart.skif.storetest.service.endringslogg.EndringsloggService#findEndringer}).
 * Objektet inneholder en liste av funne EndringId-er, en optional liste med tilhørende bobleobjekter, et statusflagg
 * som angir om alle endringer ble lest.Resultat objekt for søk etter endringer
 */
public class Endringer<E extends Endring<?,?>> implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<E> endringList = Collections.emptyList();
    private List<BubbleObject> objects = Collections.emptyList();
    private boolean alleEndringerFunnet;
    private AbstractEndringId<?> sisteEndringIdProsessert;

    public Endringer() {
    }

    public Endringer(List<E> endringList) {
        this.endringList = endringList;
    }

    public List<E> getEndringList() {
        return endringList;
    }

    public void setEndringList(List<E> endringList) {
        this.endringList = endringList;
    }

    public List<BubbleObject> getObjects() {
        return objects;
    }

    public void setObjects(List<BubbleObject> objects) {
        this.objects = objects;
    }

    public boolean isAlleEndringerFunnet() {
        return alleEndringerFunnet;
    }

    public void setAlleEndringerFunnet(boolean alleEndringerFunnet) {
        this.alleEndringerFunnet = alleEndringerFunnet;
    }

    public AbstractEndringId<?> getSisteEndringIdProsessert() {
        return sisteEndringIdProsessert;
    }

    public void setSisteEndringIdProsessert(AbstractEndringId<?> sisteEndringIdProsessert) {
        this.sisteEndringIdProsessert = sisteEndringIdProsessert;
    }

    public List<BubbleId<?>> getEndretBubbleIds() {
        ArrayList<BubbleId<?>> endretBubbleIds = Lists.newArrayListWithCapacity(endringList.size());
        for (E endring : endringList) {
            endretBubbleIds.add(endring.getEndretBubbleId());
        }
        return endretBubbleIds;
    }
}