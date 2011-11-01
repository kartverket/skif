package no.statkart.skif.store.kodelistesupport;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public abstract class DbKodeImpl extends KodeImpl implements DbKode {
    private Map<String, String> lokalisertBeskrivelse = new HashMap<String,String>();

    @Override
    public DbKodeImplId<?> getId() {
        return (DbKodeImplId<?>) super.getId();
    }

    @Override
    public Map<String, String> getLokalisertBeskrivelse() {
        return lokalisertBeskrivelse;
    }

    @Override
    public void setLokalisertBeskrivelse(Map<String, String> lokalisertBeskrivelse) {
        this.lokalisertBeskrivelse = lokalisertBeskrivelse;
    }

}
