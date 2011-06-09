package no.statkart.skif.skiftest.service.testex;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.skiftest.exception.SimpleException;
import no.statkart.skif.skiftest.exception.SimpleNonMappedException;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public class TestExServiceImpl implements TestExService {
    private final Provider<TestExService> indirectProvider;

    @Inject
    public TestExServiceImpl(Provider<TestExService> indirectProvider) {
        this.indirectProvider = indirectProvider;
    }

    @Override
    public String noTx(String exceptionClass, String message) throws SimpleException, SimpleNonMappedException {
        String methodName="noTx";
        if (exceptionClass.isEmpty()) return message.isEmpty() ? methodName : message + " " + methodName;
        throw createException(exceptionClass, message, methodName);

    }

    @Override
    public String requiresTx(String exceptionClass, String message) throws SimpleException, SimpleNonMappedException {
        String methodName="requiresTx";
        if (exceptionClass.isEmpty()) return message.isEmpty() ? methodName : message + " " + methodName;
        throw createException(exceptionClass, message, methodName);
    }

    @Override
    public String newTx(String exceptionClass, String message) throws SimpleException, SimpleNonMappedException {
        String methodName="newTx";
        if (exceptionClass.isEmpty()) return message.isEmpty() ? methodName : message + " " + methodName;
        throw createException(exceptionClass, message, "newTx");
    }

    @Override
    public String nonMappedCall(String exceptionClass, String message) throws SimpleException, SimpleNonMappedException {
        throw new ImplementationException("Denne metode kalles aldrig");
    }

    @Override
    public String indirectNoTx(List<String> callSpec, String exceptionClass, String message) throws SimpleException, SimpleNonMappedException {
        return callIndirect(callSpec, exceptionClass, message, "indirectNoTx");
    }


    @Override
    public String indirectRequiresTx(List<String> callSpec, String exceptionClass, String message) throws SimpleException, SimpleNonMappedException {
        return callIndirect(callSpec, exceptionClass, message, "indirectRequiresTx");
    }

    @Override
    public String indirectNewTx(List<String> callSpec, String exceptionClass, String message) throws SimpleException, SimpleNonMappedException {
        return callIndirect(callSpec, exceptionClass, message, "indirectNewTx");
    }

    @Override
    public String indirectNoEx(List<String> callSpec, String exceptionClass, String message) {
        try {
            return callIndirect(callSpec, exceptionClass, message, "indirectNoEx");
        } catch (SimpleException e) {
            throw new ImplementationException(e);
        } catch (SimpleNonMappedException e) {
            throw new ImplementationException(e);
        }
    }

    private SimpleException createException(String exceptionClass, String message, String methodName) throws SimpleException, SimpleNonMappedException {
        message = message.isEmpty() ? methodName : message + " " + methodName;
        Class<?> cl = SkifUtil.classForName(exceptionClass);
        if (cl == SimpleException.class) {
            throw new SimpleException(message, "infoFieldText");
        } else if (cl == SimpleNonMappedException.class) {
            throw new SimpleNonMappedException(message, "infoFieldText");
        } else {
            try {
                Throwable t = (Throwable) cl.getConstructor(String.class).newInstance(message.isEmpty() ? methodName : message + " " + methodName);
                if (t instanceof RuntimeException) {
                    throw (RuntimeException) t;
                } else {
                    throw new RuntimeException("Exception must be a RuntimeException:" + exceptionClass);
                }
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String callIndirect(List<String> callSpec, String exceptionClass, String message, String method) throws SimpleException, SimpleNonMappedException {
        message = message.isEmpty() ? method : message + " " + method;
        if (callSpec.isEmpty()) {
            return message;
        } else {
            String nextCall = callSpec.get(0);
            if (nextCall.equals("noTx")) {
                return indirectProvider.get().noTx(exceptionClass, message);
            } else if (nextCall.equals("requiresTx")) {
                return indirectProvider.get().requiresTx(exceptionClass, message);
            } else if (nextCall.equals("newTx")) {
                return indirectProvider.get().newTx(exceptionClass, message);
            } else if (nextCall.equals("indirectNoTx")) {
                return indirectProvider.get().indirectNoTx(callSpec.subList(1, callSpec.size()), exceptionClass, message);
            } else if (nextCall.equals("indirectRequiresTx")) {
                return indirectProvider.get().indirectRequiresTx(callSpec.subList(1, callSpec.size()), exceptionClass, message);
            } else if (nextCall.equals("indirectNewTx")) {
                return indirectProvider.get().indirectNewTx(callSpec.subList(1, callSpec.size()), exceptionClass, message);
            } else if (nextCall.equals("indirectNoEx")) {
                return indirectProvider.get().indirectNoEx(callSpec.subList(1, callSpec.size()), exceptionClass, message);
            } else {
                throw new ImplementationException("Wrong method in argumet:" + nextCall);
            }
        }
    }
}
