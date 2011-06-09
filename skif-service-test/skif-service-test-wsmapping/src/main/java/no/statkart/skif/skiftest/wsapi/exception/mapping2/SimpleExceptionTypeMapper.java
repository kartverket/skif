package no.statkart.skif.skiftest.wsapi.exception.mapping2;

import no.statkart.skif.mapper.MappingException;
import no.statkart.skif.skiftest.wsapi.exception.SimpleException;
import no.statkart.skif.skiftest.wsapi.exception.SimpleFaultInfo;

import javax.xml.ws.WebFault;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


/**
 * Mapper for SimpleException som viser hvordan man mapper exceptions som arver direkte fra java.lang.Exception
 *
     * @author Henrik Fredholm
     * @since 1.1
 */
public class SimpleExceptionTypeMapper<WsapiT extends SimpleException, WsapiTInfo extends SimpleFaultInfo, DomainT extends no.statkart.skif.skiftest.exception.SimpleException> extends AbstractSkifTestExceptionTypeMapper<WsapiT, DomainT> {

    private Class<WsapiTInfo> wsapiFaultInfoClass;

    public SimpleExceptionTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass, Class<WsapiTInfo> wsapiFaultInfoClass) {
        super(wsapiClass, domainClass);
        this.wsapiFaultInfoClass = wsapiFaultInfoClass;
    }


    @Override
    protected DomainT getInitialDomainObject(WsapiT source) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<DomainT> tConstructor = getDomainClass().getDeclaredConstructor(String.class, String.class);
        tConstructor.setAccessible(true);
        Throwable rootCause = tConstructor.newInstance(source.getMessage(), source.getFaultInfo().getInfoField());
        return (DomainT) rootCause;
    }

    @Override
    protected WsapiT getInitialWsapiObject(DomainT source) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        Class<WsapiT> targetClass = getWsapiClass();

        if (targetClass.getAnnotation(WebFault.class) != null) {
            try {
                WsapiTInfo faultInfo = wsapiFaultInfoClass.newInstance();
                faultInfo.setInfoField(source.getInfoField());
                return targetClass.getConstructor(String.class, wsapiFaultInfoClass, Throwable.class).newInstance(source.getMessage(), faultInfo, source.getCause());
            } catch (Throwable e) {
                throw new MappingException("No known instaniation for exception class: " + targetClass.getName());
            }
        } else {
            throw new MappingException("TargetClas not a @WebFault! class:" + targetClass.getName());
        }
    }
}
