package no.statkart.skif.store.kodelistesupport;

import java.util.Map;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public interface DbBubbleKode extends BubbleKode {
    public DbBubbleKodeId<?> getId();
    public Map<String, String> getLokalisertBeskrivelse();
    public void setLokalisertBeskrivelse(Map<String, String> lokalisertBeskrivelse);
}
