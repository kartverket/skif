package no.statkart.skif.storetest.domain.tinglysing;

import java.util.Set;

/**
 * @author Oddbjørn Kvalsund
 */
public class RettsstiftelseOgMatrikkelenheterOgAndeler {
    private Rettsstiftelse rettsstiftelse;
    private Set<NivaaIMatrikkelenhetId<?>> gjelderKunNivaaIMatrikkelenheter;
    private Set<NivaaIMatrikkelenhetId<?>> gjelderKunNivaaIMatrikkelenheterHistorisk;
    private Set<AndelIMatrikkelenhetId<?>> gjelderKunAndeler;
    private Set<AndelIMatrikkelenhetId<?>> gjelderKunAndelerHistorisk;

}
