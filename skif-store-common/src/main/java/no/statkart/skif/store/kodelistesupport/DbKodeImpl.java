package no.statkart.skif.store.kodelistesupport;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public abstract class DbKodeImpl extends KodeImpl {
    private Map<String, String> lokalisertBeskrivelse = new HashMap<String,String>();

    @Override
    public DbKodeImplId<?> getId() {
        return (DbKodeImplId<?>) super.getId();
    }

    public Map<String, String> getLokalisertBeskrivelse() {
        return lokalisertBeskrivelse;
    }

    public void setLokalisertBeskrivelse(Map<String, String> lokalisertBeskrivelse) {
        this.lokalisertBeskrivelse = lokalisertBeskrivelse;
    }

}
