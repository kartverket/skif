package no.statkart.skif.store.kodelistesupport;

import java.util.Map;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public interface DbKode extends Kode {
    public DbKodeId<?> getId();
    public Map<String, String> getLokalisertBeskrivelse();
    public void setLokalisertBeskrivelse(Map<String, String> lokalisertBeskrivelse);
}
