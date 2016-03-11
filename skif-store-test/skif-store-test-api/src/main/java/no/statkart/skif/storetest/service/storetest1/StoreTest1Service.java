package no.statkart.skif.storetest.service.storetest1;

/**
 * Enkel test service som ikke har ServiceContext parameter, som ikke kaller andre tjenester og som bruker en
 * identity mapping for å mappe mellom domene og Web Service modell.
 * <p/>
 * Servicen har metoder for å lagre key-value par til database og hente disse opp igjen. Implementasjonen bruker
 * ikke store, men kun en hibernate session som injectes via en provider
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface StoreTest1Service {

    String put(String key, String value);

    String get(String key);

    String remove(String key);

    void clear();

    String putThatFails(String key, String value);

    String putViaJDBCConnection(String key, String value);

}
