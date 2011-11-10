package no.statkart.skif.service.chain;

import no.statkart.skif.service.proxy.ProxyHandler;

import javax.annotation.Nullable;

/**
 * Factory for å lage en {@code ServiceChain} eller en del av en {@code ServiceChain}. Subklasser av dette interfacet
 * implementerer forskjellige typer {@code ServiceChain}s.
 *
 * En service {@code S} kan ha tilknyttet flere forskjellige typer {@code ServiceChain}s som hver spiller sin rolle
 * i utførelsen av et servicekall.
 *
 * <ul>
 *     <li>{@code CallServiceChain}: Utføres i forkant av alle servicekall på klient og server. På klienten termineres
 *     den av en remoting {@code ProxyHandler} som sender kallet videre til serveren. Dersom remotingen skjer via
 *     Web Services må kjeden mappen api-klasser til wsapi-klasser. Nå et servicekall utføres på
 *     serveren som serveren selv implementeres så termineres kjeden ved å sende kallet videre til
 *     enten service EJB eller service-implementasjonen. Kall som implementeres av serveren selv  marshalles
 *     ikke via Web Services.
 *     <li>{@code WSServiceChain}: Utføres som del av servicens Web Service implementasjon. Denne kjede har bl.a ansvar
 *     for å mappe wsapi-klasser til api-klasser. Termineres ved å sende kallet videre til enten service EJB eller
 *     til service-implementasjon.
 *     <li>{@code EJBServiceChain}: Utføres som del av servicens EJB implementasjon. Denne kjede har bl.a ansvar for
 *     å transaksjonshåndtering. Kjeden termineres ved å sende kallet videre til implementasjonen.
 *     <li>{@code ImplementationServiceChain}: Utføres som en del av service-implementasjonen. Kjeden avsluttes ved å
 *     sende kallet videre til selve implementasjonsklassen.
 * </ul>
 *
 * @see  ProxyHandler
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface ServiceChainFactory<S> {

    /**
     * Returer et ikke negativ tall som angir rekkefølgen {@code ServiceChainFactory}-instanser skal kalles i når en
     * {@code ServiceChain} skal bygges opp ut fra et fra et sett av {@code ServiceChainFactory}-instanser.
     * En factory som returnerer vedien 0 indikerer at den kun kan stå sist i en {@code ServiceChain}.
     * Det kan kun være en slik factory i ett {@code ServiceChainFactory}-sett.
     */
    public float getChainPosition();

    /**
     * Oppretter en kjede av {@code ProxyHandler}s og returnerer første element i kjeden.
     */
    public abstract ProxyHandler<S> createChain();

    /**
     * Oppretter en {@code ServiceChain} som legges foran en eksisterende {@code ServiceChain} gitt ved
     * {@code firstInChain}. Returnerer den nye {@code ServiceChain} representert ved den forreste
     * {@code ProxyHandler}.
     */
    public abstract ProxyHandler<S> extendChain(@Nullable ProxyHandler<S> firstInChain);
}
