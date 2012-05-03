package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.storetest.domain.kodeliste.StoreTestEnumKode;

/**
 * @author rorchr
 */
public class MatrikkelenhetsnivaaKode extends StoreTestEnumKode {

    private String ident;

    @Override
    public MatrikkelenhetsnivaaKodeId getId() {
        return (MatrikkelenhetsnivaaKodeId) super.getId();
    }

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }
}
