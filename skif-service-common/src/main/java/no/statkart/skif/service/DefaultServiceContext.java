package no.statkart.skif.service;

import java.util.Locale;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class DefaultServiceContext implements ServiceContext {
    static final long serialVersionUID = 1L;

    private String systemVersion="1.0";
    private Locale locale = new Locale("nb", "NO");

    @Override
    public String getSystemVersion() {
        return systemVersion;
    }

    @Override
    public void setSystemVersion(String systemVersion) {
        this.systemVersion= systemVersion;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
