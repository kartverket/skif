package no.statkart.skif.store.kodelistesupport;

import java.util.Map;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public interface DbBubbleKodeliste extends BubbleKodeliste {
    public DbBubbleKodelisteId<?> getId();

    public Class<? extends DbBubbleKode> getKodeClass();

    public void setKodeClass(Class<? extends DbBubbleKode> kodeClass);

    public String getKodeClassname();

    public void setKodeClassname(String kodeClassname);

    public Map<String, String> getLokalisertBeskrivelse();

    public void setLokalisertBeskrivelse(Map<String, String> lokalisertBeskrivelse);

}
