package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.0
 */
public class StoreTestBubbleIdTypeMapper<WsapiT extends no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId, DomainT extends StoreTestBubbleId> extends AbstractStoreTestTypeMapper<WsapiT,DomainT> {

    private Class idValueClass;

    public StoreTestBubbleIdTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
        super(wsapiClass, domainClass);
    }

    @Override
    public WsapiT mapDomainObject(DomainT source) {
        WsapiT target = createWsapiT();
        target.setValue(getMapping().d2w(source.getStringValue()));
        target.setSnapshotVersion(getMapping().d2w(source.getSnapshotVersion()));
        return target;
    }

    @Override
    public DomainT mapWsapiObject(WsapiT source) {
        SnapshotVersion snapshotVersion = getMapping().w2d(source.getSnapshotVersion());
        Class idValueType = getIdValueType(getDomainClass());
        Object value= parseType(idValueType, source.getValue());
        DomainT target = BubbleIds.createInstance(getDomainClass(), value, snapshotVersion);
        return target;
    }


    private Class getIdValueType(Class<DomainT> domainClass) {
        if (idValueClass==null) {
            idValueClass = BubbleIds.getValueType(domainClass);
        }
        return idValueClass;
    }

    private <T> T parseType(Class<T> idValueType, String value) {
        if (idValueType == Long.class) {
            return idValueType.cast(Long.parseLong(value));
        } else {
            // Det er en String;
            return idValueType.cast(value);
        }
    }
}