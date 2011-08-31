package no.statkart.skif.persistence;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interface for manager som kan gi ut connections og som har til oppgave å holde styr på hvilke connections som
 * har blitt gitt ut slik at disse kan frigis på et senere tidspunkt. Manageren bruker nøkler til holde styr på de forskjellige
 * connections som den administrerer. Hvilke nøkler som kan brukes avhenger av manager implementasjonen.
 *
 * Hvilken implementasjon som er hentiktsmessig å bruke avhenger av hvilken kontekst servicen utføres i.
 * Eksempler på på parametre som kan være med til å bestemme kontekst:
 * <ul>
 *    <li>Bruker tjenesten Bean Managed Persistence eller Container Managed Persistende
 *    <li>Kjører tjenesten i JEE eller SingleVm mode
 *    <li>Bruker tjenesten Store rammeverket eller er bruker den bare service delen av skif rammeverket
 * </ul>
 *
 * Hver av disse varianter krever forskjellig implementasjon.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface ConnectionFactory {
    Connection createConnection() throws SQLException;
}
