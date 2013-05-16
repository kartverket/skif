package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.store.KodelisteTransfer;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleList;
import no.statkart.skif.storetest.wsapi.domain.kodeliste.KodeIdList;
import no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteIdList;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class KodelisteTransferTypeMapper<WsapiT extends no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteTransfer, DomainT extends KodelisteTransfer> extends AbstractStoreTestTypeMapper<WsapiT,DomainT> {
    public KodelisteTransferTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
        super(wsapiClass, domainClass);
    }

    @Override
    public void mapDomainObject(DomainT source, WsapiT target) {
        target.setKodelisteIds(map.d2w(source.getKodelisteIds(), KodelisteIdList.class));
        target.setObjects(map.d2w(source.getObjects().values(), StoreTestBubbleList.class));
    }

    @Override
    public void mapWsapiObject(WsapiT source, DomainT target) {
    }

    @Override
    protected DomainT getInitialDomainObject(WsapiT source) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ArrayList kodelistIds = map.w2d(source.getKodelisteIds(), ArrayList.class);
        ArrayList objects = map.w2d(source.getObjects(), ArrayList.class);
        Constructor<DomainT> constructor = getDomainClass().getConstructor(List.class, Collection[].class);
        DomainT domainT = constructor.newInstance(kodelistIds, new Collection[]{objects});
        return domainT;
    }
}