package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class StoreTestBubbleIdTypeMapper<WsapiT extends no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId, DomainT extends StoreTestBubbleId> extends AbstractStoreTestTypeMapper<WsapiT,DomainT> {

    private Class idValueClass;

    public StoreTestBubbleIdTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
        super(wsapiClass, domainClass);
    }

    @Override
    public void mapDomainObject(DomainT source, WsapiT target) {
        super.mapDomainObject(source, target);
        target.setValue(map.d2w(source.getStringValue()));
        target.setSnapshotVersion(map.d2w(source.getSnapshotVersion()));
    }

    @Override
    protected DomainT getInitialDomainObject(WsapiT source) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        SnapshotVersion snapshotVersion = map.w2d(source.getSnapshotVersion());
        Class idValueType = getIdValueType(getDomainClass());
        Object value= parseType(idValueType, source.getValue());
        DomainT target = getDomainClass().getConstructor(idValueType, SnapshotVersion.class).newInstance(value, snapshotVersion);
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