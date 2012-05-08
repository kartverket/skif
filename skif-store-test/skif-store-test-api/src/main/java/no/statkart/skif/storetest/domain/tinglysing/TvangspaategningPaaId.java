package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.storetest.domain.tinglysing.kobling.RettsstiftelsePersonRolle;

import java.util.Set;

/**
 * @author Oddbjørn Kvalsund
 */
public class TvangspaategningPaaId extends PaategningPaaAndel {
    private Set<PersonId<?>> tvangspaategnetIds = this.rettsstiftelsePersonIdsKoblinger.get(RettsstiftelsePersonRolle.TVANGSPAATEGNET);
    private Set<RettsstiftelseOgMatrikkelenheter> gjelderLeieavtalerOgRettigheter; // TODO: Vent på Christian sitt opplegg
    private Set<RettsstiftelseOgMatrikkelenheter> gjelderLeieavtalerOgRettigheterHistorisk;  // TODO: Vent på Christian sitt opplegg

    public PersonId<?> getTvangspaategnetId() {
        return tvangspaategnetIds.isEmpty() ? null : tvangspaategnetIds.iterator().next();
    }

    public void setTvangspaategnetId(PersonId<?> tvangspaategnetIds) {
        this.tvangspaategnetIds.clear();
        this.tvangspaategnetIds.add(tvangspaategnetIds);
    }

    public Person getTvangspaategnet() {
        if(tvangspaategnetIds.isEmpty()) {
            return null;
        } else {
            return store().get(tvangspaategnetIds.iterator().next());
        }
    }
}