package no.statkart.skif.service;

import java.io.Serializable;
import java.util.Locale;

/**
 * Holder på parametre som normalt ikke endrer seg og må være med i alle kall, enten direkte eller indirekte.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface ServiceContext extends Serializable {
    /**
     * Den konkrete versjonen av systemet som kaller er laget for å gå mot. Dette brukes for å kompensere for mindre
     * endringer i API-er.
     *
     * @return et versjonsnummer
     */
    String getSystemVersion();

    /**
     * Angir den konkrete versjonen av systemet som kaller er laget for å gå mot. Dette brukes for å kompensere for mindre
     * endringer i API-er.
     *
     * @param systemVersion    versjonsnummer API-et skal etterligne
     */
    void setSystemVersion(String systemVersion);


    Locale getLocale();

    void setLocale(Locale locale);
}
