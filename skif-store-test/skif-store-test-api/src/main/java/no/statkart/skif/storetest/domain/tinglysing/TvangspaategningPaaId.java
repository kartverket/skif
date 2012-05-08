package no.statkart.skif.storetest.domain.tinglysing;

import java.util.Set;

/**
 * @author Oddbjørn Kvalsund
 */
public class TvangspaategningPaaId extends PaategningPaaAndel {
    private PersonId<?> tvangspaategnet;
    private Set<RettsstiftelseOgMatrikkelenheter> gjelderLeieavtalerOgRettigheter;
    private Set<RettsstiftelseOgMatrikkelenheter> gjelderLeieavtalerOgRettigheterHistorisk;
}