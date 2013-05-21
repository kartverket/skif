package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.mapper.MappingException;
import no.statkart.skif.store.KodelisteTransfer;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleList;
import no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteIdList;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.0
 */
public class KodelisteTransferTypeMapper<WsapiT extends no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteTransfer, DomainT extends KodelisteTransfer> extends AbstractStoreTestTypeMapper<WsapiT,DomainT> {
    public KodelisteTransferTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
        super(wsapiClass, domainClass);
    }

    @Override
    public WsapiT mapDomainObject(DomainT source) {
        WsapiT target = createWsapiT();
        target.setKodelisteIds(getMapping().d2w(source.getKodelisteIds(), KodelisteIdList.class));
        target.setObjects(getMapping().d2w(source.getObjects().values(), StoreTestBubbleList.class));
        return target;
    }

    @Override
    public DomainT mapWsapiObject(WsapiT source) {
        ArrayList kodelistIds = getMapping().w2d(source.getKodelisteIds(), ArrayList.class);
        ArrayList objects = getMapping().w2d(source.getObjects(), ArrayList.class);
        try {
            Constructor<DomainT> constructor = getDomainClass().getConstructor(List.class, Collection[].class);
            DomainT domainT = constructor.newInstance(kodelistIds, new Collection[]{objects});
            return domainT;
        } catch (NoSuchMethodException e) {
            throw new MappingException("No suitable constructor in " + getDomainClass());
        } catch (InstantiationException e) {
            throw new MappingException("Could not instantiate " + getDomainClass());
        } catch (IllegalAccessException e) {
            throw new MappingException("Could not instantiate " + getDomainClass());
        } catch (InvocationTargetException e) {
            throw new MappingException("Could not instantiate " + getDomainClass());
        }
    }
}