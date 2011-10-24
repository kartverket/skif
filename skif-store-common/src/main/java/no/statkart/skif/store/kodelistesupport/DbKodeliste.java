package no.statkart.skif.store.kodelistesupport;

import java.util.Map;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public interface DbKodeliste extends Kodeliste {
    public DbKodelisteId<?> getId();

    public Class<? extends DbKode> getKodeClass();

    public void setKodeClass(Class<? extends DbKode> kodeClass);

    public String getKodeClassname();

    public void setKodeClassname(String kodeClassname);

    public Map<String, String> getLokalisertBeskrivelse();

    public void setLokalisertBeskrivelse(Map<String, String> lokalisertBeskrivelse);
}
