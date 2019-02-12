package no.statkart.skif.storetest.service.uow;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.StoreBubbleTransfer;
import no.statkart.skif.storetest.domain.basic.SimpleId;

/**
 * Service for å støtte UnitOfWork testing
 *
 * @author Henrik Fredholm
 * @since 2.9
 */
public interface UowTestService {

    StoreBubbleTransfer findAndLock(BubbleId bubbleId);

    /**
     * Oppdaterer en Simple boble ved å sette ny tekst på boblen i en egen transasksjon. Dette for å kunne teste
     * refersh av en boble etter at den har blitt lest ifm at boblen låses eller vanlig refresh. Siden metoden vil
     * frigir alle låser for inneværende bruker bør kun kalles når brukeren ikke har eksistrende låser. Det testes
     * for dette i metoden slik at den feiler hvis den brukes når dette ikke er tilfellet.
     * oppdatering
     */
    void updateTextInNewTransaction(SimpleId<?> simpleId, String text);

    int antallLaaserForBruker();
}
