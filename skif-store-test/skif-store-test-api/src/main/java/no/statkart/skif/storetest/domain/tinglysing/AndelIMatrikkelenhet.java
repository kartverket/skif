package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubble;

/**
 * @since 2.1
 */
public class AndelIMatrikkelenhet extends AbstractStoreTestBubble {
    private NivaaIMatrikkelenhetId nivaaIMatrikkelenhetId;
    private int teller;
    private int nevner;
    private PersonId andelseierPersonId;
    private NivaaIMatrikkelenhetId andelseierNivaaIMatrikkelenhetId;
    private String status;
}
