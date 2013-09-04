package no.statkart.skif.wsversioning.wsapi.v2.exception.mapping;

import no.statkart.skif.exception.ServerException;
import no.statkart.skif.exception.SkifException;
import no.statkart.skif.mapper.MappingException;
import no.statkart.skif.wsversioning.wsapi.v2.exception.ExceptionDetail;
import no.statkart.skif.wsversioning.wsapi.v2.exception.ExceptionProperties;
import no.statkart.skif.wsversioning.wsapi.v2.exception.ServiceException;
import no.statkart.skif.wsversioning.wsapi.v2.exception.ServiceFaultInfo;
import no.statkart.skif.wsversioning.wsapi.v2.exception.StackTraceElementList;

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
 * @author Tor Egil R. Strand
 * @since 2.0
 */
public class ServiceExceptionTypeMapper<WsapiT extends ServiceException, WsapiTInfo extends ServiceFaultInfo, DomainT extends SkifException> extends AbstractServiceExceptionTypeMapper<WsapiT, DomainT> {

    private Class<WsapiTInfo> wsapiFaultInfoClass;

    public ServiceExceptionTypeMapper(Map<String, Class<? extends DomainT>> exceptionClassMap, Class<WsapiT> wsapiClass, Class<DomainT> domainClass, Class<WsapiTInfo> wsapiFaultInfoClass) {
        super(wsapiClass, domainClass, exceptionClassMap);
        this.wsapiFaultInfoClass = wsapiFaultInfoClass;
    }


    @Override
    public DomainT mapWsapiObject(WsapiT source) {
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
        DomainT target = createDomainException(source, rootExceptionDetail, cause);
        target.setStackTrace(generateStackTraceElements(rootExceptionDetail.getStackTraceElements()));

        ServiceFaultInfo faultInfo = source.getFaultInfo();
        target.setFeilkode(faultInfo.getFeilkode());
        target.setFeilkodebeskrivelse(faultInfo.getFeilkodebeskrivelse());

        return target;
    }

    private DomainT createDomainException(WsapiT source, ExceptionDetail rootExceptionDetail, Throwable cause) {
        Class<? extends DomainT> domainClass = findDomainClass(source);
        try {
            Constructor<? extends DomainT> tConstructor = domainClass.getDeclaredConstructor(String.class, Throwable.class);
            tConstructor.setAccessible(true);
            return tConstructor.newInstance(rootExceptionDetail.getMessage(), cause);
        } catch (NoSuchMethodException e) {
            throw new MappingException("Could not find requested constructor for " + domainClass);
        } catch (InstantiationException e) {
            throw new MappingException("Could not instantiate " + domainClass);
        } catch (IllegalAccessException e) {
            throw new MappingException("Could not instantiate " + domainClass);
        } catch (InvocationTargetException e) {
            throw new MappingException("Could not instantiate " + domainClass);
        }
    }

    private Throwable createServerException(ExceptionDetail exceptionDetail, Throwable cause) {
        String message = "-> " + exceptionDetail.getClassName() + ": " + exceptionDetail.getMessage();
        ServerException serverException = new ServerException(message, cause);
        serverException.setStackTrace(generateStackTraceElements(exceptionDetail.getStackTraceElements()) );
        return serverException;
    }


    @Override
    public WsapiT mapDomainObject(DomainT source) {
        try {
            WsapiTInfo faultInfo = wsapiFaultInfoClass.newInstance();
            faultInfo.setCategory(findCategory(source));
            faultInfo.setFeilkode(source.getFeilkode());
            faultInfo.setFeilkodebeskrivelse(source.getFeilkodebeskrivelse());
            faultInfo.setStackTraceText(generateStacktraceString(source));
            faultInfo.setExceptionDetail(generateExceptionDetail(source));
            faultInfo.setProperties(new ExceptionProperties());
            return createWsException(source, faultInfo);
        } catch (InstantiationException e) {
            throw new MappingException(e);
        } catch (IllegalAccessException e) {
            throw new MappingException(e);
        }
    }


    protected WsapiT createWsException(DomainT source, WsapiTInfo faultInfo) {
        Class<WsapiT> targetClass = getWsapiClass();

        if (targetClass.getAnnotation(WebFault.class) != null) {
            if (source instanceof SkifException) {
                try {
                    Constructor<WsapiT> constructor = targetClass.getConstructor(String.class, ServiceFaultInfo.class, Throwable.class);
                    return constructor.newInstance(source.getMessage(), faultInfo, source.getCause());
                } catch (NoSuchMethodException e) {
                    throw new MappingException("Could not find requested constructor for " + targetClass);
                } catch (InvocationTargetException e) {
                    throw new MappingException("Could not instantiate " + targetClass);
                } catch (InstantiationException e) {
                    throw new MappingException("Could not instantiate " + targetClass);
                } catch (IllegalAccessException e) {
                    throw new MappingException("Could not instantiate " + targetClass);
                }
            } else {
                throw new MappingException("Expected source to be derived from: " + SkifException.class);
            }
        } else {
            throw new MappingException("TargetClass not a @WebFault class:" + targetClass.getName());
        }
    }





    // methods that may be subclassed for extended mapping ->

    /**
     * @return default nested string of print stacktrace with nested exceptions
     */

    protected static StackTraceElement[] generateStackTraceElements(StackTraceElementList stackTraceElementList) {
        List<no.statkart.skif.wsversioning.wsapi.v2.exception.StackTraceElement> stackTraceElements = stackTraceElementList.getItem();
        StackTraceElement[] mappedStackTraceElements = new StackTraceElement[stackTraceElements.size()];
        for (int i = 0; i < mappedStackTraceElements.length; i++) {
            no.statkart.skif.wsversioning.wsapi.v2.exception.StackTraceElement stackTraceElement = stackTraceElements.get(i);
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
            no.statkart.skif.wsversioning.wsapi.v2.exception.StackTraceElement targetElement = new no.statkart.skif.wsversioning.wsapi.v2.exception.StackTraceElement();
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
