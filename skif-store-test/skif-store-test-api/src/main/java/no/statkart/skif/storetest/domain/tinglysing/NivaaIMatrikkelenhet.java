package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubble;

/**
 * @author rorchr
 * @since 2.1
 */
public class NivaaIMatrikkelenhet extends AbstractStoreTestBubble {
    private MatrikkelenhetId<?> matrikkelenhetId;
    private MatrikkelenhetsnivaaKodeId matrikkelenhetsnivaaKodeId;

    @Override
    public NivaaIMatrikkelenhetId<?> getId() {
        return (NivaaIMatrikkelenhetId<?>) super.getId();
    }

    public MatrikkelenhetId<?> getMatrikkelenhetId() {
        return matrikkelenhetId;
    }

    public void setMatrikkelenhetId(MatrikkelenhetId<?> matrikkelenhetId) {
        this.matrikkelenhetId = matrikkelenhetId;
    }

    public MatrikkelenhetsnivaaKodeId getMatrikkelenhetsnivaaKodeId() {
        return matrikkelenhetsnivaaKodeId;
    }

    public MatrikkelenhetsnivaaKode getMatrikkelenhetsnivaaKode() {
        return store().get(matrikkelenhetsnivaaKodeId);
    }

    public void setMatrikkelenhetsnivaaKodeId(MatrikkelenhetsnivaaKodeId matrikkelenhetsnivaaKodeId) {
        this.matrikkelenhetsnivaaKodeId = matrikkelenhetsnivaaKodeId;
    }
}
