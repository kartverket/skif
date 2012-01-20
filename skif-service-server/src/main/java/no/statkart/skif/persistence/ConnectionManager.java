package no.statkart.skif.persistence;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interface for manager object som kan gi ut connections og som har til oppgave å holde styr på hvilke connections som
 * har blitt gitt ut slik at disse kan frigis på et senere tidspunkt. Manageren bruker nøkler til holde styr på de forskjellige
 * connections som den administrerer. Hvilke nøkler som kan brukes avhenger av manager implementasjonen.
 *
 * Hvilken implementasjon som er hentiktsmessig å bruke avhenger av hvilken kontekst connection manageren skal virker i.
 * Eksempler på på parametre som er med til å bestemme kontekst for connection manager:
 * <ul>
 *    <li>skal manageren undersøtte bean managed persistence eller container managed persistance
 *    <li>utføres koden i singlevm eller jee mode
 *    <li>skal connections som manageren gir ut fungerer sammem mend store rameverket og store session eller skal de brukes stand-alone
 * </ul>
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
@Deprecated
public interface ConnectionManager {
    Connection getConnection(Object key);
    boolean isActive(Object key);
    void close(Object key);
    void close();
    void beginTransaction();
    void commit();
    void rollback();

    Connection aquireConnection(Object key);
    void releaseConnection(Connection connection, Object key);
}
