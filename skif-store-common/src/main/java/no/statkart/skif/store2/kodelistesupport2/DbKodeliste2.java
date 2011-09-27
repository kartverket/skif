package no.statkart.skif.store2.kodelistesupport2;

import java.util.Map;

/**
 * @author Roar Ingebrigtsen
 * @since 2.5
 */
public interface DbKodeliste2 extends Kodeliste2 {
    public DbKodelisteId2<?> getId();

    public Class<? extends DbKode2> getKodeClass();

    public void setKodeClass(Class<? extends DbKode2> kodeClass);

    public String getKodeClassname();

    public void setKodeClassname(String kodeClassname);

    public Map<String, String> getLokalisertBeskrivelse();

    public void setLokalisertBeskrivelse(Map<String, String> lokalisertBeskrivelse);
}
