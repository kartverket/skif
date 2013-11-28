package no.statkart.skif.storetest.domain.endringslogg;

import no.statkart.skif.store.endringslogg.AbstractEndring;
import no.statkart.skif.store.endringslogg.AbstractEndringId;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubble;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleId;
import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * Baseklasse for alle endringer i StoreTest-prosjektet.
 *
 * For enkelt å kunne søke på subtype via Hibernate er det hensiktsmessig at det finne en endringstype per boblesubtype
 * man skal kunne søke på. Det er ikke noe krav at det skal være en en-til-en match mellom endringssubtyper og
 * boblesubtyper, men som minimum må det være en endringstype for hver boble basetype. Dermed kan klassen for
 * endringstypen brukes som filter i endringsloggmetodene.
 *
 * <P>Videre må
 *
 * @author Tor Egil R. Strand
 * @author Henrik Fredholm
 * @since 2.2.0
 */
public class Endring<I extends AbstractEndringId<?>, EI extends StoreTestBubbleId<?>> extends AbstractEndring<I, EI> implements StoreTestBubble {
    private static final long serialVersionUID = 1L;

    private String brukernavn;

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    protected Endring() {
    }

    public Endring(long value) {
        setId(new EndringId<Endring>(value));
    }

    @Override
    public I getId() {
        return super.getId();
    }

    @Override
    public EI getEndretBubbleId() {
        return super.getEndretBubbleId();
    }

    public String getBrukernavn() {
        return brukernavn;
    }

    public void setBrukernavn(String brukernavn) {
        this.brukernavn = brukernavn;
    }

}
