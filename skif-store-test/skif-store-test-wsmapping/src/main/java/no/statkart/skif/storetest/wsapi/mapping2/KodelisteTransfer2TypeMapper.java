package no.statkart.skif.storetest.wsapi.mapping2;

import no.statkart.skif.store2.KodelisteTransfer2;
import no.statkart.skif.storetest.wsapi.domain2.StoreTestBubble2List;
import no.statkart.skif.storetest.wsapi.domain2.kodeliste.KodeId2List;
import no.statkart.skif.storetest.wsapi.domain2.kodeliste.KodelisteId2List;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class KodelisteTransfer2TypeMapper<WsapiT extends no.statkart.skif.storetest.wsapi.domain2.kodeliste.KodelisteTransfer2, DomainT extends KodelisteTransfer2> extends AbstractStore2TestTypeMapper<WsapiT,DomainT> {
    public KodelisteTransfer2TypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
        super(wsapiClass, domainClass);
    }

    @Override
    public void mapDomainObject(DomainT source, WsapiT target) {
        super.mapDomainObject(source, target);
        target.setKodeIds(map.d2w(source.getKodeIds(), KodeId2List.class));
        target.setKodelisteIds(map.d2w(source.getKodelisteIds(), KodelisteId2List.class));
        target.setObjects(map.d2w(source.getObjects(), StoreTestBubble2List.class));
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