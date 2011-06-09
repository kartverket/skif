package no.statkart.skif.util;

import javax.net.ssl.HostnameVerifier;
/**
 * Denne godtar alt
 * @author Henrik Fredholm
 * @since 1.1
 */
public class NullHostnameVerifier implements HostnameVerifier {

    @Override
    public boolean verify(java.lang.String s, javax.net.ssl.SSLSession sslSession) {
        return true;
    }

}
