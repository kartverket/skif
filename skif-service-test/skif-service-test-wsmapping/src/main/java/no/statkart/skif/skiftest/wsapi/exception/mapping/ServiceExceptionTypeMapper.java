package no.statkart.skif.skiftest.wsapi.exception.mapping;

import no.statkart.skif.skiftest.wsapi.exception.*;
import no.statkart.skif.exception.ServerException;
import no.statkart.skif.exception.SkifException;
import no.statkart.skif.mapper.MappingException;

import javax.xml.ws.WebFault;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.StackTraceElement;
import java.util.List;
import java.util.Stack;


/**
 * Mapper som må instansieres for hver type exception som skal mappes ut.
 *
 * @author Leif Lislegård
 * @since 0.6
 */
public class ServiceExceptionTypeMapper<WsapiT extends ServiceException, WsapiTInfo extends ServiceFaultInfo, DomainT extends SkifException> extends AbstractServiceExceptionTypeMapper<WsapiT, DomainT> {

    private Class<WsapiTInfo> wsapiFaultInfoClass;

    public ServiceExceptionTypeMapper(Class<WsapiT> wsapiClass, Class<WsapiTInfo> wsapiFaultInfoClass, Class<DomainT> domainClass) {
        super(wsapiClass, domainClass);
        this.wsapiFaultInfoClass = wsapiFaultInfoClass;
    }


    @Override
    protected DomainT getInitialDomainObject(WsapiT source) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ExceptionDetail rootExceptionDetail = source.getFaultInfo().getExceptionDetail();
        Stack<ExceptionDetail> stack = new Stack<ExceptionDetail>();
        {
            ExceptionDetail exceptionDetail = rootExceptionDetail.getCause();
            while (exceptionDetail != null) {
                stack.push(exceptionDetail);
                exceptionDetail = exceptionDetail.getCause();
            }
        }

        Throwable cause = null;
        while (!stack.isEmpty()) {
            ExceptionDetail exceptionDetail = stack.pop();
            cause = createServerException(exceptionDetail, cause);
        }
        Constructor<DomainT> tConstructor = getDomainClass().getDeclaredConstructor(String.class, Throwable.class);
        tConstructor.setAccessible(true);
        Throwable rootCause = tConstructor.newInstance(source.getMessage(), cause);
        rootCause.setStackTrace(generateStackTraceElements(rootExceptionDetail.getStackTraceElements()));
        return (DomainT) rootCause;
    }

    private Throwable createServerException(ExceptionDetail exceptionDetail, Throwable cause) {
        String message = "-> " + exceptionDetail.getClassName() + ": " + exceptionDetail.getMessage();
        ServerException serverException = new ServerException(message, cause);
        serverException.setStackTrace(generateStackTraceElements(exceptionDetail.getStackTraceElements()) );
        return serverException;
    }


    @Override
    public void mapDomainObject(DomainT source, WsapiT target) {
        try {
            WsapiTInfo faultInfo = wsapiFaultInfoClass.newInstance();
            faultInfo.setFeilkode(source.getFeilkode());
            faultInfo.setFeilkodebeskrivelse(source.getFeilkodebeskrivelse());
            faultInfo.setStackTraceText(generateStacktraceString(source));
            faultInfo.setExceptionDetail(generateExceptionDetail(source));
            target.setFaultInfo(faultInfo);
        } catch (InstantiationException e) {
            throw new MappingException(e);
        } catch (IllegalAccessException e) {
            throw new MappingException(e);
        }
    }


    @Override
    protected WsapiT getInitialWsapiObject(DomainT source) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        Class<WsapiT> targetClass = getWsapiClass();

        if (targetClass.getAnnotation(WebFault.class) != null) {
            if (source instanceof no.statkart.skif.exception.SkifException) {
                no.statkart.skif.exception.SkifException skifException = (no.statkart.skif.exception.SkifException) source;
                for (Constructor<?> constructor : targetClass.getConstructors()) {
                    if (constructor.getParameterTypes().length == 3) {
                        try {
                            return (WsapiT) constructor.newInstance(skifException.getMessage(), null, skifException.getCause());
                        } catch (InvocationTargetException e) {
                            throw new MappingException(e);
                        }
                    }
                }
                throw new MappingException("No known instaniation for exception class: " + targetClass);
            } else {
                throw new MappingException("Expected source to be derived from: " + no.statkart.skif.exception.SkifException.class);
            }
        } else {
            throw new MappingException("TargetClas not a @WebFault! class:" + targetClass.getName());
        }
    }

    @Override
    public void mapWsapiObject(WsapiT source, DomainT target) {
    }





    // methods that may be subclassed for extended mapping2 ->

    /**
     * @return default nested string of print stacktrace with nested exceptions
     */

    protected static StackTraceElement[] generateStackTraceElements(no.statkart.skif.skiftest.wsapi.exception.StackTraceElementList stackTraceElementList) {
        List<no.statkart.skif.skiftest.wsapi.exception.StackTraceElement> stackTraceElements = stackTraceElementList.getItem();
        // Krever adaptor
        //List<no.statkart.skif.skiftest.wsapi.exception.StackTraceElement> stackTraceElements = stackTraceElementList._getList();
        StackTraceElement[] mappedStackTraceElements = new StackTraceElement[stackTraceElements.size()];
        for (int i = 0; i < mappedStackTraceElements.length; i++) {
            no.statkart.skif.skiftest.wsapi.exception.StackTraceElement stackTraceElement = stackTraceElements.get(i);
            mappedStackTraceElements[i] = new StackTraceElement(stackTraceElement.getDeclaringClass(), stackTraceElement.getMethodName(), stackTraceElement.getFileName(), stackTraceElement.getLineNumber());
        }
        return mappedStackTraceElements;
    }





    // private helper methods -->

    private ExceptionDetail generateExceptionDetail(Throwable source) {
        if (source != null) {
            ExceptionDetail root = new ExceptionDetail();
            root.setClassName(source.getClass().getName());
            root.setMessage(source.getMessage());
            root.setStackTraceElements(generateStackTraceElements(source.getStackTrace()));
            if (source.getCause() != source) {
                root.setCause(generateExceptionDetail(source.getCause()));
            }
            return root;
        } else {
            return null;
        }
    }

    private StackTraceElementList generateStackTraceElements(StackTraceElement[] stackTrace) {
        StackTraceElementList list = new StackTraceElementList();
        for (int i = 0; i < stackTrace.length; i++) {
            StackTraceElement sourceElement = stackTrace[i];
            no.statkart.skif.skiftest.wsapi.exception.StackTraceElement targetElement = new no.statkart.skif.skiftest.wsapi.exception.StackTraceElement();
            targetElement.setDeclaringClass(sourceElement.getClassName());
            targetElement.setMethodName(sourceElement.getMethodName());
            targetElement.setFileName(sourceElement.getFileName());
            targetElement.setLineNumber(sourceElement.getLineNumber());
            list.getItem().add(targetElement);
        }
        return list;
    }


    private String generateStacktraceString(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }


}
