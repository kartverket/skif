package no.statkart.skif.store2.kodelistesupport2;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Roar Ingebrigtsen
 * @since 2.5
 */
public abstract class DbKodeImpl2 extends KodeImpl2 implements DbKode2 {
    private Map<String, String> lokalisertBeskrivelse = new HashMap<String,String>();

    @Override
    public DbKodeIdImpl2<?> getId() {
        return (DbKodeIdImpl2<?>) super.getId();
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
