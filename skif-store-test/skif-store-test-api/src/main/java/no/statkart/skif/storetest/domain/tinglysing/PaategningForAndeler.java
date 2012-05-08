package no.statkart.skif.storetest.domain.tinglysing;

import java.util.Set;

/**
 * @author Oddbjørn Kvalsund
 */
public abstract class PaategningForAndeler extends Paategning {

    private Set<RettsstiftelseOgMatrikkelenheterOgAndeler> gjelder;
    private Set<RettsstiftelseOgMatrikkelenheterOgAndeler> gjelderHistorisk;
    
}