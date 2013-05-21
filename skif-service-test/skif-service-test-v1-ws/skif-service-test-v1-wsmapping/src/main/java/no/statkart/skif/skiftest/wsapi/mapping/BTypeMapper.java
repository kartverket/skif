package no.statkart.skif.skiftest.wsapi.mapping;

import no.statkart.skif.skiftest.domain.B;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class BTypeMapper<WsapiT extends no.statkart.skif.skiftest.wsapi.domain.B, DomainT extends B> extends AbstractSkifTestTypeMapper<WsapiT, DomainT> {

    protected BTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
        super(wsapiClass, domainClass);
    }

    @Override
    public WsapiT mapDomainObject(DomainT source) {
        WsapiT target = createWsapiT();
        target.setText(getMapping().d2w(source.getText()));
        return target;
    }

    @Override
    public DomainT mapWsapiObject(WsapiT source) {
        DomainT target = createDomainT();
        target.setText(getMapping().w2d(source.getText()));
        return target;
    }
}