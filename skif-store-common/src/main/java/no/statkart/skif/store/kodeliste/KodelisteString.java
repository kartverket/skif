package no.statkart.skif.store.kodeliste;

/**
 * Denne klasse har ingen felter. Alle felter ligger i Kodeliste5. Den finnes kun fordi rammeverket trenger den for
 * å jobbe med KodelisteLong5Id
 * @author Henrik Fredholm
 * @since 2.1
 */
public class KodelisteString extends AbstractKodeliste {
    private static final long serialVersionUID = 1L;

    @Override
    public KodelisteStringId<?> getId() {
        return (KodelisteStringId<?>) super.getId();
    }
}
