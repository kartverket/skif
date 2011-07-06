package no.statkart.skif.persistence;

import java.sql.Connection;

/**
 * Interface for manager som kan gi ut connections og som har til oppgave å holde styr på hvilke connections som
 * har blitt gitt ut slik at disse kan frigis på et senere tidspunkt. Manageren bruker nøkler til holde styr på de forskjellige
 * connections som den administrerer. Hvilke nøkler som kan brukes avhenger av manager implementasjonen.
 *
 * Hvilken manager implementasjon som skal brukes bestemmes av hvilken kontekst servicen som skal bruken connectionen
 * utføres i. Følgende kontekster kan være aktuelle:
 * <ul>
 *    <li>Bruker tjenesten Bean Managed Persistence eller Container Managed Persistende
 *    <li>Kjører tjenesten i JEE eller SingleVm mode
 *    <li>Bruker tjenesten Store rammeverket eller er det en stand-alone tjeneste som bruker connectionen direkte.
 * </ul>
 *
 * Hver av disse varianter krever forskjellig implementasjon.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface ConnectionManager {
    Connection getConnection(Object key);
    boolean isConnectionAllocated(Object key);
    void closeConnection(Object key);
    void closeAllAllocatedConnections();
}
