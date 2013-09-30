package no.statkart.skif.store.kodeliste;

/**
 * Interface for kodesupport som har statiske kodelister. Disse kodelistene lokaliseres fra property-filer.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public interface StaticKodelisteKodeSupport {
    String getKodelisteResourceKey();

    String getResourceMsgName();
}
