package no.statkart.skif.domain;

import javax.annotation.Nullable;

public interface EqualityHandler<O> {
    /**
     * Sammenligner objekt o1 med o2. Reglene for likhet må avklares mellom bruker og implementasjon.
     *
     * @param o1         Først objekt.
     * @param o2         Andre objekt. OBS! Det er ingen garanti for at dette objektet er av samme klasse som o1.
     *                   Dette tilsvarer {@link Object#equals(Object)}
     * @param comparator Den sammenligneren som er i bruk. Man må fortsette å bruke denne for å kunne bruke de handlers den har i tillegg til denne.
     * @return {@code true} dersom objektene anses som like
     */
    boolean checkEquals(O o1, @Nullable Object o2, EqualsByFields comparator);
}
