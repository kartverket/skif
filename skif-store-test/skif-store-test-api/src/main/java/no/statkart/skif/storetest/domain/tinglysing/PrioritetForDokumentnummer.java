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
}
