package no.statkart.skif.store.endringslogg;

/**
 * Angir om en {@link AbstractEndring} skyldes opprettelse, oppdatering eller sletting.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public enum Endringstype {
    /**
     * Kommer når objekt opprettes. Endret id angir subtype objektet hadde da det ble opprettet
     */
    Nyoppretting,

    /**
     * Kommer når et objekt endre subtype, dersom endringsloggen modellerer de to subtypene forskjellig.
     *  Endret id vil da være av gammel type. Slike endringer etterfølges alltid av en
     *  tilhørende {@link #Oppdatering} som angir ny id type.
     */
    Typeendring,

    /**
     * Kommer når et objekt oppdateres. Endret id angir subtype etter oppdatering for det endrede objektet
     */
    Oppdatering,

    /**
     * Kommer når et objekt slettes. Endret id angir  subtypen objekt hadde da det ble slettet
     */
    Sletting
}
