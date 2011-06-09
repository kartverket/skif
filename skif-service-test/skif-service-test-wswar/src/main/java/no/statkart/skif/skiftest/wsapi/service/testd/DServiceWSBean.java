package no.statkart.skif.skiftest.wsapi.service.testd;

import com.google.inject.Injector;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.service.ws.SkifWebService;
import no.statkart.skif.skiftest.wsapi.config.SkifTestWebServiceInjectorConfig;
import no.statkart.skif.skiftest.wsapi.domain.StringList;
import no.statkart.skif.skiftest.wsapi.exception.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import java.lang.StackTraceElement;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
@WebService(
        name = "DService",
        serviceName = "DServiceWS",
        targetNamespace = "http://skif.statkart.no/skiftest/wsapi/service/testd")
public class DServiceWSBean extends SkifWebService<DServiceWSI> implements DServiceWSI {
    @Resource
    private WebServiceContext ctx;

    private DServiceWSI wsServiceChain;

    public DServiceWSBean() {
        super(DServiceWSI.class);
    }

    @PostConstruct
    protected void init() {
        Injector injector = SkifTestWebServiceInjectorConfig.getWebServiceInjector();
        wsServiceChain = getServiceImplementation(injector, ctx);
    }

    @Override
    public String noTx(@WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws ServiceException, SystemException, ImplementationException, OperationalException, ApplicationException, FinderException, ValidationException {
        return wsServiceChain.noTx(exceptionClass, message);
    }

    @Override
    public String noTxNested(@WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws ServiceException, SystemException, ImplementationException, OperationalException, ApplicationException, FinderException, ValidationException {
        return wsServiceChain.noTxNested(exceptionClass, message);
    }

    @Override
    public String requiresTx(@WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws ServiceException, SystemException, ImplementationException, OperationalException, ApplicationException, FinderException, ValidationException {
        return wsServiceChain.requiresTx(exceptionClass, message);
    }

    @Override
    public String newTx(@WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws ServiceException, SystemException, ImplementationException, OperationalException, ApplicationException, FinderException, ValidationException {
        return wsServiceChain.newTx(exceptionClass, message);
    }

    /**
     * Denne klassen kaster exceptions uten å kalle rammeverket, skal at man kan teste hvordan rammeverket håndtere ukjendte exceptions
     */
    public String nonMappedWSCall(@WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws ServiceException, SystemException, ImplementationException, OperationalException, ApplicationException, FinderException, ValidationException, SimpleNonMappedException {
        if (exceptionClass.isEmpty()) {
            return message;
        } else if (exceptionClass.equals(ImplementationException.class.getName())) {
            ImplementationFaultInfo info = new ImplementationFaultInfo();
            Throwable ex = new no.statkart.skif.exception.ImplementationException();
            ex.fillInStackTrace();
            StackTraceElement stElement = ex.getStackTrace()[0];
            ExceptionDetail detail = new ExceptionDetail();
            no.statkart.skif.skiftest.wsapi.exception.StackTraceElement element = new no.statkart.skif.skiftest.wsapi.exception.StackTraceElement();
            element.setDeclaringClass(stElement.getClass().getName());
            element.setFileName(stElement.getFileName());
            element.setLineNumber(100);
            element.setMethodName("nonMappedCall");
            detail.setClassName(this.getClass().getName());
            detail.setMessage("message");
            StackTraceElementList elementList = new StackTraceElementList();
            elementList.getItem().add(element);
            detail.setStackTraceElements(elementList);
            info.setExceptionDetail(detail);
            info.setFeilkode("feilkode");
            info.setFeilkodebeskrivelse("beskrivelse");
            info.setStackTraceText("stracktraceText");
            throw new ImplementationException(message, info);
        } else if (exceptionClass.equals(SimpleNonMappedException.class.getName())) {
            SimpleNonMappedFaultInfo info = new SimpleNonMappedFaultInfo();
            info.setInfoField("infoFieleMessage");
            throw new SimpleNonMappedException(message, info);
        } else {
            try {
                throw (RuntimeException) SkifUtil.classForName(exceptionClass).getConstructor(String.class).newInstance(message);
            } catch (InstantiationException e) {
                throw new no.statkart.skif.exception.ImplementationException(e);
            } catch (IllegalAccessException e) {
                throw new no.statkart.skif.exception.ImplementationException(e);
            } catch (InvocationTargetException e) {
                throw new no.statkart.skif.exception.ImplementationException(e);
            } catch (NoSuchMethodException e) {
                throw new no.statkart.skif.exception.ImplementationException(e);
            }
        }
    }

    @Override
    public String nonMappedEJBCall(@WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws ServiceException, SystemException, ImplementationException, OperationalException, ApplicationException, FinderException, ValidationException {
        return wsServiceChain.nonMappedEJBCall(exceptionClass, message);
    }

    @Override
    public String indirectNoTx(@WebParam(name = "callSpec") StringList callSpec, @WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws ServiceException, SystemException, ImplementationException, OperationalException, ApplicationException, FinderException, ValidationException {
        return wsServiceChain.indirectNoTx(callSpec, exceptionClass, message);
    }

    @Override
    public String indirectRequiresTx(@WebParam(name = "callSpec") StringList callSpec, @WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws ServiceException, SystemException, ImplementationException, OperationalException, ApplicationException, FinderException, ValidationException {
        return wsServiceChain.indirectRequiresTx(callSpec, exceptionClass, message);
    }

    @Override
    public String indirectNewTx(@WebParam(name = "callSpec") StringList callSpec, @WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) throws ServiceException, SystemException, ImplementationException, OperationalException, ApplicationException, FinderException, ValidationException {
        return wsServiceChain.indirectNewTx(callSpec, exceptionClass, message);
    }

    @Override
    public String indirectNoEx(@WebParam(name = "callSpec") StringList callSpec, @WebParam(name = "exceptionClass") String exceptionClass, @WebParam(name = "message") String message) {
        return wsServiceChain.indirectNoEx(callSpec, exceptionClass, message);
    }

}
