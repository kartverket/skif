package no.statkart.skif.store2.kodelistesupport2;

import no.statkart.skif.exception.ImplementationException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Roar Ingebrigtsen
 * @since 2.5
 */
public abstract class DbKodelisteImpl2 extends KodelisteImpl2 implements DbKodeliste2 {
    private Class<? extends DbKode2> kodeClass;
    private Map<String, String> lokalisertBeskrivelse = new HashMap<String,String>();

    private String beskrivelsesKey;

    @Override
    public DbKodelisteIdImpl2 getId() {
        return (DbKodelisteIdImpl2) super.getId();
    }

    public String getBeskrivelsesKey() {
        return beskrivelsesKey;
    }

    public void setBeskrivelsesKey(String beskrivelsesKey) {
        this.beskrivelsesKey = beskrivelsesKey;
    }

    @Override
    public Class<? extends DbKode2> getKodeClass() {
        return kodeClass;
    }

    @Override
    public void setKodeClass(Class<? extends DbKode2> kodeClass) {
        this.kodeClass = kodeClass;
    }

    public String getKodeClassname() {
        return kodeClass.getName();
    }

    public void setKodeClassname(String kodeClassname) {
        try {
            this.kodeClass = (Class<? extends DbKode2>) Class.forName(kodeClassname);
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
