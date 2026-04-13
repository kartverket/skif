package no.statkart.skif.store;

import jakarta.annotation.Nullable;

/**
 * Objekter som har ident må implementere dette interface for å støtte relasjonscaching for identer.
 */
public interface BubbleObjectWithIdent<I> extends BubbleObject {

    /**
     * Returnerer objektes ident eller {@code null} hvis identen beregnes på basis av felter i andre objekter som er {@code null}
     */
    @Nullable
    I getIdent();

    /**
     * Callback metode som må kalles når objektets ident felter har blitt endret. Hvis flere felter endres samtidig
     * kalles metoden når alle feltene er endret og ikke ette hvert feltendringen. Dette fordi objektets mellomtilstand
     * ikke er representativ for objektets ident og kan representere en eksisterende ident for et annet objekt.
     * <p/>
     * Metoden kalles automatisk av Store-rammeverket ved enabling av relasjonscache eller hvis en object instans
     * byttes ut med en ny instans.
     * <p/>
     * Implementasjonen av metoden bør kalle {@code Bubbles.onChangeRelation} med den nye Identen. Hvis objektets
     * felter inngår som del av en ident for et annet objekt bør metoden også kalle videre på {@code onIdentChanged} for
     * disse objektene.
     */
    void onIdentChanged();

}
