package no.statkart.skif.wsversioning.wsapi.v1.mapping;

import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.wsversioning.domain.WSVersioningBubbleId;

/**
 * Mapper for bobleid-er.
 * 
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class WSVersioningBubbleIdTypeMapper<WsapiT extends no.statkart.skif.wsversioning.wsapi.v1.domain.WSVersioningBubbleId, DomainT extends WSVersioningBubbleId> extends AbstractWSVersioningTypeMapper<WsapiT,DomainT> {

    public WSVersioningBubbleIdTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
        super(wsapiClass, domainClass);
    }

    @Override
    public WsapiT mapDomainObject(DomainT source) {
        WsapiT target = createWsapiT();
        target.setValue((Long) source.getValue());
        target.setSnapshotVersion(getMapping().d2w(source.getSnapshotVersion()));
        return target;
    }

    @Override
    public DomainT mapWsapiObject(WsapiT source) {
        SnapshotVersion snapshotVersion = getMapping().w2d(source.getSnapshotVersion());
        DomainT target = BubbleIds.createInstance(getDomainClass(), source.getValue(), snapshotVersion);
        return target;
    }

}
