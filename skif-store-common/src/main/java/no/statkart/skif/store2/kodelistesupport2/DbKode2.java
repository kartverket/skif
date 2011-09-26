package no.statkart.skif.store2.kodelistesupport2;

import java.util.Map;

/**
 * @author Roar Ingebrigtsen
 * @since 2.5
 */
public interface DbKode2 extends Kode2 {
    public DbKodeId2<?> getId();
    public Map<String, String> getLokalisertBeskrivelse();
    public void setLokalisertBeskrivelse(Map<String, String> lokalisertBeskrivelse);
}
