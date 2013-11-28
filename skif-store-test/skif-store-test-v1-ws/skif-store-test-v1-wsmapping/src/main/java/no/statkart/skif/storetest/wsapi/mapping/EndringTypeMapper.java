package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.mapper.MappingException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.endringslogg.Endringstype;
import no.statkart.skif.storetest.domain.endringslogg.Endring;

import java.lang.reflect.InvocationTargetException;

/**
 * Mapper mellom {@code wsapi:Endring} og {@code domainT:Endring}. Det må installeres en instans av mapperen
 * for hver domainT subtype som skal mappes til sin egen wsapiT subtype. Subtyper som skal slås sammen håndteres
 * av samme mapper instans som må være definert for supertypen av alle typer som skal slås sammen.
 *
 * @author Henrik Fredholm
 * @since 2.4.0
 */
public class EndringTypeMapper<WsapiT extends no.statkart.skif.storetest.wsapi.domain.endringslogg.Endring, DomainT extends Endring> extends AbstractStoreTestTypeMapper<WsapiT, DomainT> {

    protected EndringTypeMapper(Class<WsapiT> wsapiTClass, Class<DomainT> domainClass) {
        super(wsapiTClass, domainClass);
    }

    @Override
    public WsapiT mapDomainObject(DomainT source) {
        WsapiT target = createWsapiT();
        target.setId(getMapping().d2w(source.getId(),no.statkart.skif.storetest.wsapi.domain.endringslogg.EndringId.class));
        target.setEndretBubbleId(getMapping().d2w(source.getEndretBubbleId()));
        target.setEndringstidspunkt(getMapping().d2w(source.getEndringstidspunkt()));
        target.setEndringstype(getMapping().d2w(source.getEndringstype(), no.statkart.skif.storetest.wsapi.domain.endringslogg.Endringstype.class));
        return target;
    }

    @Override
    public DomainT mapWsapiObject(WsapiT source) {
        DomainT domainT = createDomainT();
        Class<? extends BubbleId<DomainT>> bubbleIdClass = BubbleIds.getBubbleIdClass(getDomainClass());
        domainT.setId(getMapping().w2d(source.getId()));
        domainT.setEndretBubbleId(getMapping().w2d(source.getEndretBubbleId()));
        domainT.setEndringstype(getMapping().w2d(source.getEndringstype(), Endringstype.class));
        domainT.setEndringstidspunkt(getMapping().w2d(source.getEndringstidspunkt()));
        return domainT;
    }

    public static <WsapiT extends no.statkart.skif.storetest.wsapi.domain.endringslogg.Endring, DomainT extends Endring>  EndringTypeMapper<WsapiT, DomainT> create(Class<WsapiT> wsapiTClass, Class<DomainT> domainClass) {
        return new EndringTypeMapper<WsapiT, DomainT>(wsapiTClass, domainClass);
    }
}
