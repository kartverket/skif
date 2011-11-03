package no.statkart.skif.store.kodelistesupport;

import no.statkart.skif.exception.ImplementationException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public abstract class DbKodelisteImpl extends KodelisteImpl  {
    private Class<? extends DbKode> kodeClass;
    private Map<String, String> lokalisertBeskrivelse = new HashMap<String,String>();

    private String beskrivelsesKey;

    @Override
    public DbKodelisteImplId<?> getId() {
        return (DbKodelisteImplId) super.getId();
    }

    public String getBeskrivelsesKey() {
        return beskrivelsesKey;
    }

    public void setBeskrivelsesKey(String beskrivelsesKey) {
        this.beskrivelsesKey = beskrivelsesKey;
    }

    public Class<? extends DbKode> getKodeClass() {
        return kodeClass;
    }

    public void setKodeClass(Class<? extends DbKode> kodeClass) {
        this.kodeClass = kodeClass;
    }

    public String getKodeClassname() {
        return kodeClass.getName();
    }

    public void setKodeClassname(String kodeClassname) {
        try {
            this.kodeClass = (Class<? extends DbKode>) Class.forName(kodeClassname);
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
