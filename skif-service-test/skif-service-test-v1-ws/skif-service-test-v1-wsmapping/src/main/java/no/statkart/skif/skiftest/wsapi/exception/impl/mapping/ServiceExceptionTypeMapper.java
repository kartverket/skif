package no.statkart.skif.skiftest.wsapi.exception.impl.mapping;

import no.statkart.skif.exception.ServerException;
import no.statkart.skif.exception.SkifException;
import no.statkart.skif.mapper.AbstractTypeMapper;
import no.statkart.skif.mapper.MappingException;
import no.statkart.skif.skiftest.wsapi.exception.ServiceException;
import no.statkart.skif.skiftest.wsapi.exception.impl.ExceptionDetail;
import no.statkart.skif.skiftest.wsapi.exception.impl.ServiceFaultInfo;
import no.statkart.skif.skiftest.wsapi.exception.impl.StackTraceElementList;

import java.io.PrintWriter;
import java.io.StringWriter;
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
public class ServiceExceptionTypeMapper extends AbstractTypeMapper<ServiceException, SkifException, SkifTestExceptionMapping> {
    private final Map<String, Class<? extends SkifException>> exceptionClassMap;

    public ServiceExceptionTypeMapper(Map<String, Class<? extends SkifException>> exceptionClassMap) {
        super(ServiceException.class, SkifException.class, SkifTestExceptionMapping.class);
        this.exceptionClassMap = exceptionClassMap;
    }


    @Override
    public SkifException mapWsapiObject(ServiceException source) {
        ExceptionDetail rootExceptionDetail = source.getFaultInfo().getExceptionDetail();
        Stack<ExceptionDetail> stack = new Stack<>();
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
        SkifException target = getMapping().w2d(source.getFaultInfo(), SkifException.class);
        target.initCause(cause);
        StackTraceElement[] serverStackTrace = generateStackTraceElements(rootExceptionDetail.getStackTraceElements());
        StackTraceElement[] clientStackTrace = source.getStackTrace();
        StackTraceElement[] combinedStackTrace = new StackTraceElement[serverStackTrace.length + clientStackTrace.length];
        System.arraycopy(serverStackTrace, 0, combinedStackTrace, 0, serverStackTrace.length);
        System.arraycopy(clientStackTrace, 0, combinedStackTrace, serverStackTrace.length, clientStackTrace.length);
        target.setStackTrace(combinedStackTrace);

        return target;
    }

    private Throwable createServerException(ExceptionDetail exceptionDetail, Throwable cause) {
        String message = "-> " + exceptionDetail.getClassName() + ": " + exceptionDetail.getMessage();
        ServerException serverException = new ServerException(message, cause);
        serverException.setStackTrace(generateStackTraceElements(exceptionDetail.getStackTraceElements()));
        return serverException;
    }


    @Override
    public ServiceException mapDomainObject(SkifException source) {
        ServiceFaultInfo faultInfo = getMapping().d2w(source, ServiceFaultInfo.class);
        faultInfo.setCategory(findCategory(source));
        faultInfo.setExceptionDetail(generateExceptionDetail(source));
        faultInfo.setStackTraceText(generateStacktraceString(source));
        return new ServiceException(source.getMessage(), faultInfo, source);
    }





    // methods that may be subclassed for extended mapping ->

    /**
     * @param stackTraceElementList stack trace elementer
     * @return default nested string of print stacktrace with nested exceptions
     */
    protected static java.lang.StackTraceElement[] generateStackTraceElements(StackTraceElementList stackTraceElementList) {
        List<no.statkart.skif.skiftest.wsapi.exception.impl.StackTraceElement> stackTraceElements = stackTraceElementList.getItem();
        java.lang.StackTraceElement[] mappedStackTraceElements = new java.lang.StackTraceElement[stackTraceElements.size()];
        for (int i = 0; i < mappedStackTraceElements.length; i++) {
            no.statkart.skif.skiftest.wsapi.exception.impl.StackTraceElement stackTraceElement = stackTraceElements.get(i);
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
            //noinspection ObjectEquality
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
        for (StackTraceElement sourceElement : stackTrace) {
            no.statkart.skif.skiftest.wsapi.exception.impl.StackTraceElement targetElement = new no.statkart.skif.skiftest.wsapi.exception.impl.StackTraceElement();
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


    private String findCategory(SkifException source) {
        Class<? extends SkifException> sourceClass = source.getClass();
        for (Map.Entry<String, Class<? extends SkifException>> entry : exceptionClassMap.entrySet()) {
            if (entry.getValue().isAssignableFrom(sourceClass)) {
                return entry.getKey();
            }
        }
        throw new MappingException("Could not find category for exception class: " + source.getClass().getName()); //skal ikke kunne forekomme
    }
}
