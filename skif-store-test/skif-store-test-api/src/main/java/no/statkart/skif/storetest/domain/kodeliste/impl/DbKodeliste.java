package no.statkart.skif.storetest.domain.kodeliste.impl;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.kodelistesupport.DbBubbleKode;
import no.statkart.skif.store.kodelistesupport.DbBubbleKodeliste;
import no.statkart.skif.storetest.domain.kodeliste.Kodeliste;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class DbKodeliste extends Kodeliste implements DbBubbleKodeliste {
    private Class<? extends DbBubbleKode> kodeClass;
    private String kodeClassname;
    private Map<String, String> lokalisertBeskrivelse = new HashMap<String,String>();

    @Override
    public DbKodelisteId getId() {
        return (DbKodelisteId) super.getId();
    }

    @Override
    public Class<? extends DbBubbleKode> getKodeClass() {
        return kodeClass;
    }

    @Override
    public void setKodeClass(Class<? extends DbBubbleKode> kodeClass) {
        this.kodeClass = kodeClass;
    }

    public String getKodeClassname() {
        return kodeClass.getName();
    }

    public void setKodeClassname(String kodeClassname) {
        try {
            this.kodeClass = (Class<? extends DbBubbleKode>) Class.forName(kodeClassname);
        } catch (ClassNotFoundException e) {
            throw new ImplementationException(e);
        }
    }

    public Map<String, String> getLokalisertBeskrivelse() {
        return lokalisertBeskrivelse;
    }

    public void setLokalisertBeskrivelse(Map<String, String> lokalisertBeskrivelse) {
        this.lokalisertBeskrivelse = lokalisertBeskrivelse;
    }
}
