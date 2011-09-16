package no.statkart.skif.storetest.domain.kodeliste.impl;

import no.statkart.skif.store.kodelistesupport.DbBubbleKode;
import no.statkart.skif.storetest.domain.kodeliste.Kode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public abstract class DbKode extends Kode implements DbBubbleKode {
    private Map<String, String> lokalisertBeskrivelse = new HashMap<String,String>();

    @Override
    public DbKodeId<?> getId() {
        return (DbKodeId<?>) super.getId();
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
