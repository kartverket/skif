package no.statkart.skif.storetest.domain.tinglysing;

import java.util.Set;
import no.statkart.skif.storetest.domain.tinglysing.kobling.RettsstiftelseRettsstiftelseRolle;

/**
 * @author Knut Inge Bøe
 */
public class PrioritetForDokumentnummer  extends PaategningForMatrikkelenheter {
    private Set<RettsstiftelseId<?>> sidestiltMed = rettsstiftelseRettsstiftelseIdsKoblinger.get(RettsstiftelseRettsstiftelseRolle.SIDESTILT_MED_AKTIV);
    private Set<RettsstiftelseId<?>> sidestiltMedHistorisk = rettsstiftelseRettsstiftelseIdsKoblinger.get(RettsstiftelseRettsstiftelseRolle.SIDESTILT_MED_HISTORISK);
    private Set<RettsstiftelseId<?>> veketFor = rettsstiftelseRettsstiftelseIdsKoblinger.get(RettsstiftelseRettsstiftelseRolle.VEKET_FOR_AKTIV);
    private Set<RettsstiftelseId<?>> veketForHistorisk = rettsstiftelseRettsstiftelseIdsKoblinger.get(RettsstiftelseRettsstiftelseRolle.VEKET_FOR_HISTORISK);


    public Set<RettsstiftelseId<?>> getSidestiltMed() {
        return sidestiltMed;
    }

    public void setSidestiltMed(Set<RettsstiftelseId<?>> sidestiltMed) {
        this.sidestiltMed.clear();
        this.sidestiltMed.addAll(sidestiltMed);
    }

    public Set<RettsstiftelseId<?>> getSidestiltMedHistorisk() {
        return sidestiltMedHistorisk;
    }

    public void setSidestiltMedHistorisk(Set<RettsstiftelseId<?>> sidestiltMedHistorisk) {
        this.sidestiltMedHistorisk.clear();
        this.sidestiltMedHistorisk.addAll(sidestiltMedHistorisk);
    }

    public Set<RettsstiftelseId<?>> getVeketFor() {
        return veketFor;
    }

    public void setVeketFor(Set<RettsstiftelseId<?>> veketFor) {
        this.veketFor.clear();
        this.veketFor.addAll(veketFor);
    }

    public Set<RettsstiftelseId<?>> getVeketForHistorisk() {
        return veketForHistorisk;
    }

    public void setVeketForHistorisk(Set<RettsstiftelseId<?>> veketForHistorisk) {
        this.veketForHistorisk.clear();
        this.veketForHistorisk.addAll(veketForHistorisk);
    }
}
