package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.storetest.domain.kodeliste.StoreTestEnumKode;

/**
 * @author rorchr
 */
public class RettstypeKode extends StoreTestEnumKode {

    private String ident;

    @Override
    public RettstypeKodeId getId() {
        return (RettstypeKodeId) super.getId();
    }

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }
}
