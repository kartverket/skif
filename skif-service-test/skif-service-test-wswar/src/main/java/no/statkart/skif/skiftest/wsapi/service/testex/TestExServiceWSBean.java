package no.statkart.skif.skiftest.wsapi.service.testex;

import com.google.inject.Injector;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.skiftest.wsapi.exception.SimpleNonMappedException;
import no.statkart.skif.skiftest.wsapi.config.SkifTestWebServiceInjectorConfig;
import no.statkart.skif.skiftest.wsapi.domain.StringList;
import no.statkart.skif.skiftest.wsapi.exception.SimpleException;
import no.statkart.skif.skiftest.wsapi.exception.SimpleFaultInfo;
import no.statkart.skif.skiftest.wsapi.exception.SimpleNonMappedFaultInfo;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
@WebService(
        name = "TestExService",
        serviceName = "TestExServiceWS",
        targetNamespace = "http://skif.statkart.no/skiftest/wsapi/service/testex")
public class TestExServiceWSBean extends SkifWebService<TestExServiceWSI> implements TestExServiceWSI {
    @Resource
    private WebServiceContext ctx;

    private TestExServiceWSI wsServiceChain;

    public TestExServiceWSBean() {
        super(TestExServiceWSI.class);
    }

    @PostConstruct
    protected void init() {
        Injector injector = SkifTestWebServiceInjectorConfig.getWebServiceInjector();
        wsServiceChain = getServiceImplementation(injector, ctx);
    }

    @Override
    public String noTx(@WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws SimpleException, SimpleNonMappedException {
        return wsServiceChain.noTx(exceptionClass, message);
    }

    @Override
    public String requiresTx(@WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws SimpleException, SimpleNonMappedException {
        return wsServiceChain.requiresTx(exceptionClass, message);
    }

    @Override
    public String newTx(@WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws SimpleException, SimpleNonMappedException {
        return wsServiceChain.newTx(exceptionClass, message);
    }

    /**
     * Denne klassen kaster exceptions uten å kalle rammeverket, skal at man kan teste hvordan rammeverket håndtere ukjendte exceptions
     */
    public String nonMappedCall(@WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws SimpleException, SimpleNonMappedException {
        if (exceptionClass.isEmpty()) {
           return message;
        } else if (exceptionClass.equals(SimpleException.class.getName())) {
            SimpleFaultInfo info = new SimpleFaultInfo();
            info.setInfoField("infoFieldMessage");
            throw new SimpleException(message, info);
        } else if (exceptionClass.equals(SimpleNonMappedException.class.getName())) {
            SimpleNonMappedFaultInfo info = new SimpleNonMappedFaultInfo();
            info.setInfoField("infoFieleMessage");
            throw new SimpleNonMappedException(message, info);
        } else {
            try {
                throw (RuntimeException) SkifUtil.classForName(exceptionClass).getConstructor(String.class).newInstance(message);
            } catch (InstantiationException e) {
                throw new ImplementationException(e);
            } catch (IllegalAccessException e) {
                throw new ImplementationException(e);
            } catch (InvocationTargetException e) {
                throw new ImplementationException(e);
            } catch (NoSuchMethodException e) {
                throw new ImplementationException(e);
            }
        }
    }

    @Override
    public String indirectNoTx(@WebParam(name = "callSpec") StringList callSpec, @WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws SimpleException, SimpleNonMappedException {
        return wsServiceChain.indirectNoTx(callSpec, exceptionClass, message);
    }

    @Override
    public String indirectRequiresTx(@WebParam(name = "callSpec") StringList callSpec, @WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws SimpleException, SimpleNonMappedException {
        return wsServiceChain.indirectRequiresTx(callSpec, exceptionClass, message);
    }

    @Override
    public String indirectNewTx(@WebParam(name = "callSpec") StringList callSpec, @WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws SimpleException, SimpleNonMappedException {
        return wsServiceChain.indirectNewTx(callSpec, exceptionClass, message);
    }

    @Override
    public String indirectNoEx(@WebParam(name = "callSpec") StringList callSpec, @WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) {
        return wsServiceChain.indirectNoEx(callSpec, exceptionClass, message);
    }
}
