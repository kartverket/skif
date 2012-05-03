package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKode;

/**
 * @author rorchr
 */
public class RettsstiftelsestypeKode extends StoreTestDbKode {

    private RettstypeKodeId rettstypeKodeId;

    @Override
    public RettsstiftelsestypeKodeId getId() {
        return (RettsstiftelsestypeKodeId) super.getId();
    }

    public RettstypeKodeId getRettstypeKodeId() {
        return rettstypeKodeId;
    }

    public void setRettstypeKodeId(RettstypeKodeId rettstypeKodeId) {
        this.rettstypeKodeId = rettstypeKodeId;
    }
}
