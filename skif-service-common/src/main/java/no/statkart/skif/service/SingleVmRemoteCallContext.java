package no.statkart.skif.service;

import java.util.Map;

/**
 * Denne klassen brukes i {@code SINGLE_VM} mode til å overføre data fra klient til server som ikke
 * overføres via parametre i kallet. Dette gjelder i hovedsak overførsel av brukernavn og passord,
 * men kan også omfatte annen data som f.eks en {@code ServiceContext} parameter. I JEE mode brukes ikke denne
 * klassen siden her går kallet via tilhørende Web Service som sender brukernavn og passord via http authentisering
 * og overfører {@code ServiceContext}'en som en ekstra siste paramter i kallet.
 * <p/>
 * Klassen bruker et {@code Map<String, Object>} dataobjekt for mest mulig fleksibel overføring av data via klassen.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class SingleVmRemoteCallContext {
    private Map<String, Object> contextData;

    public SingleVmRemoteCallContext() {
    }

    public SingleVmRemoteCallContext(Map<String, Object> dataMap) {
        this.contextData = dataMap;
    }

    public Map<String, Object> getContextData() {
        return contextData;
    }
}
