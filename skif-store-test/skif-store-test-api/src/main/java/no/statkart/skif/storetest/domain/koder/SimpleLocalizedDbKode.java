package no.statkart.skif.storetest.domain.koder;

/**
 * Historisk databasekode med ett lokalisert felt og ett ikke-lokalisert felt (ganske typisk kode for matrikkelen).
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class SimpleLocalizedDbKode extends HistoriskDbKode {
    private static final long serialVersionUID = 1L;

    @Override
    public SimpleLocalizedDbKodeId getId() {
        return (SimpleLocalizedDbKodeId) super.getId();
    }
}
