package no.statkart.skif.service;

import java.io.Serializable;
import java.util.Locale;

/**
 * Holder på parametre som normalt ikke endre seg og må være med i alle kald, enten direkte eller indirekte
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface ServiceContext extends Serializable {
    String getSystemVersion();
    void setSystemVersion(String systemVersion);

    Locale getLocale();
    void setLocale(Locale locale);
}
