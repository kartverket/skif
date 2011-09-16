package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.storetest.domain.kodeliste.KodelisteTransfer;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleList;
import no.statkart.skif.storetest.wsapi.domain.kodeliste.KodeIdList;
import no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteIdList;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class KodelisteTransferTypeMapper<WsapiT extends no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteTransfer, DomainT extends KodelisteTransfer> extends AbstractStoreTestTypeMapper<WsapiT,DomainT> {
    public KodelisteTransferTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
        super(wsapiClass, domainClass);
    }

    @Override
    public void mapDomainObject(DomainT source, WsapiT target) {
        super.mapDomainObject(source, target);
        target.setKodeIds(map.d2w(source.getKodeIds(), KodeIdList.class));
        target.setKodelisteIds(map.d2w(source.getKodelisteIds(), KodelisteIdList.class));
        target.setObjects(map.d2w(source.getObjects(), StoreTestBubbleList.class));
    }

    @Override
    public void mapWsapiObject(WsapiT source, DomainT target) {
        super.mapWsapiObject(source, target);
    }

    @Override
    protected DomainT getInitialDomainObject(WsapiT source) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ArrayList kodeIds = map.w2d(source.getKodeIds(), new ArrayList());
        ArrayList kodelistIds = map.w2d(source.getKodelisteIds(), new ArrayList());
        ArrayList objects = map.w2d(source.getObjects(), new ArrayList());
        DomainT domainT = getDomainClass().getConstructor(Collection.class, Collection.class, Collection.class).newInstance(kodeIds, kodelistIds, objects);
        return domainT;
    }
}