package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubble;

/**
 * @since 2.1
 */
public class NivaaIMatrikkelenhet extends AbstractStoreTestBubble {
    private MatrikkelenhetId matrikkelenhetId;
    private String nivaa;

    @Override
    public NivaaIMatrikkelenhetId<?> getId() {
        return (NivaaIMatrikkelenhetId<?>) super.getId();
    }
}
