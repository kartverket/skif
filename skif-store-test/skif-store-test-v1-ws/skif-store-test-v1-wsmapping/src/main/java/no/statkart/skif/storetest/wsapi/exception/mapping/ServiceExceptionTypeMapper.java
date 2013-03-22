package no.statkart.skif.storetest.wsapi.exception.mapping;

import no.statkart.skif.exception.ServerException;
import no.statkart.skif.exception.SkifException;
import no.statkart.skif.mapper.MappingException;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;
import no.statkart.skif.storetest.wsapi.exception.*;

import javax.xml.ws.WebFault;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Mapper som må instansieres for hver type exception som skal mappes ut.
 *
 * @author Leif Lislegård
 * @since 2.0
 */
public class ServiceExceptionTypeMapper<WsapiT extends ServiceException, WsapiTInfo extends ServiceFaultInfo, DomainT extends SkifException> extends AbstractServiceExceptionTypeMapper<WsapiT, DomainT> {

    private Class<WsapiTInfo> wsapiFaultInfoClass;

    public ServiceExceptionTypeMapper(Map<String, Class<DomainT>> exceptionClassMap, Class wsapiClass, Class domainClass, Class wsapiFaultInfoClass) {
        super(wsapiClass, domainClass, exceptionClassMap);
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
        Constructor<DomainT> tConstructor = findDomainClass(source).getDeclaredConstructor(String.class, Throwable.class);
        tConstructor.setAccessible(true);
        Throwable rootCause = tConstructor.newInstance(rootExceptionDetail.getMessage(), cause);
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
            faultInfo.setCategory(findCategory(source));
            faultInfo.setFeilkode(source.getFeilkode());
            faultInfo.setFeilkodebeskrivelse(source.getFeilkodebeskrivelse());
            faultInfo.setStackTraceText(generateStacktraceString(source));
            faultInfo.setExceptionDetail(generateExceptionDetail(source));
            faultInfo.setProperties(new ExceptionProperties());
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
            if (source instanceof SkifException) {
                SkifException skifException = source;
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
                throw new MappingException("Expected source to be derived from: " + SkifException.class);
            }
        } else {
            throw new MappingException("TargetClas not a @WebFault! class:" + targetClass.getName());
        }
    }

    @Override
    public void mapWsapiObject(WsapiT source, DomainT target) {
        ServiceFaultInfo faultInfo = source.getFaultInfo();
        if (faultInfo != null) {
            target.setFeilkode(faultInfo.getFeilkode());
            target.setFeilkodebeskrivelse(faultInfo.getFeilkodebeskrivelse());
        }
    }





    // methods that may be subclassed for extended mapping ->

    /**
     * @return default nested string of print stacktrace with nested exceptions
     */

    protected static java.lang.StackTraceElement[] generateStackTraceElements(StackTraceElementList stackTraceElementList) {
        List<no.statkart.skif.storetest.wsapi.exception.StackTraceElement> stackTraceElements = stackTraceElementList.getItem();
        java.lang.StackTraceElement[] mappedStackTraceElements = new java.lang.StackTraceElement[stackTraceElements.size()];
        for (int i = 0; i < mappedStackTraceElements.length; i++) {
            no.statkart.skif.storetest.wsapi.exception.StackTraceElement stackTraceElement = stackTraceElements.get(i);
            mappedStackTraceElements[i] = new java.lang.StackTraceElement(stackTraceElement.getDeclaringClass(), stackTraceElement.getMethodName(), stackTraceElement.getFileName(), stackTraceElement.getLineNumber());
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

    private StackTraceElementList generateStackTraceElements(java.lang.StackTraceElement[] stackTrace) {
        StackTraceElementList list = new StackTraceElementList();
        for (int i = 0; i < stackTrace.length; i++) {
            java.lang.StackTraceElement sourceElement = stackTrace[i];
            no.statkart.skif.storetest.wsapi.exception.StackTraceElement targetElement = new no.statkart.skif.storetest.wsapi.exception.StackTraceElement();
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
