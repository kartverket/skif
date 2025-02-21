package no.statkart.skif.skiftest.service.testd;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.exception.FinderException;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.SkifException;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class DServiceImpl implements DService {
    private final Provider<DService> indirectProvider;

    @Inject
    public DServiceImpl(Provider<DService> indirectProvider) {
        this.indirectProvider = indirectProvider;
    }

    @Override
    public String noTx(String exceptionClass, String message) throws SkifException {
        String methodName = "noTx";
        if (exceptionClass.isEmpty()) return message.isEmpty() ? methodName : message + " " + methodName;
        throw createException(exceptionClass, message, methodName);
    }


    @Override
    public String noTxNested(String exceptionClass, String message) throws SkifException {
        String methodName = "noTx";
        if (exceptionClass.isEmpty()) return message.isEmpty() ? methodName : message + " " + methodName;
        throw createNestedException(exceptionClass, message, methodName);

    }

    @Override
    public String requiresTx(String exceptionClass, String message) throws SkifException {
        String methodName = "requiresTx";
        if (exceptionClass.isEmpty()) return message.isEmpty() ? methodName : message + " " + methodName;
        throw createException(exceptionClass, message, methodName);
    }

    @Override
    public String newTx(String exceptionClass, String message) throws SkifException {
        String methodName = "newTx";
        if (exceptionClass.isEmpty()) return message.isEmpty() ? methodName : message + " " + methodName;
        throw createException(exceptionClass, message, "newTx");
    }

    @Override
    public String nonMappedWSCall(String exceptionClass, String message) throws SkifException {
        throw new ImplementationException("Denne metode kalles aldrig");
    }

    @Override
    public String nonMappedEJBCall(String exceptionClass, String message) throws SkifException {
        throw new ImplementationException("Denne metode kalles aldrig");
    }

    @Override
    public String indirectNoTx(List<String> callSpec, String exceptionClass, String message) throws SkifException {
        return callIndirect(callSpec, exceptionClass, message, "indirectNoTx");
    }


    @Override
    public String indirectRequiresTx(List<String> callSpec, String exceptionClass, String message) throws SkifException {
        return callIndirect(callSpec, exceptionClass, message, "indirectRequiresTx");
    }

    @Override
    public String indirectNewTx(List<String> callSpec, String exceptionClass, String message) throws SkifException {
        return callIndirect(callSpec, exceptionClass, message, "indirectNewTx");
    }

    @Override
    public String indirectNoEx(List<String> callSpec, String exceptionClass, String message) {
        return callIndirect(callSpec, exceptionClass, message, "indirectNoEx");
    }

    private RuntimeException createException(String exceptionClass, String message, String methodName) {
        message = message.isEmpty() ? methodName : message + " " + methodName;
        Class<?> cl = SkifUtil.classForName(exceptionClass);
        if (cl == ImplementationException.class) {
            return new ImplementationException(message).setFeilkode("feilkode").setFeilkodebeskrivelse("feilkodebeskrivelse");
        } else if (cl == FinderException.class) {
            final FinderException finderException = new FinderException(message).setFeilkode("feilkode").setFeilkodebeskrivelse("feilkodebeskrivelse");
            return finderException;
        } else {
            try {
                Throwable t = (Throwable) cl.getConstructor(String.class).newInstance(message);
                t.fillInStackTrace(); // Må til for å stacktrace riktig
                if (t instanceof RuntimeException) {
                    return (RuntimeException) t;
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

    private RuntimeException createNestedException(String exceptionClass, String message, String methodName) throws SkifException {
       RuntimeException cause = createException(exceptionClass, "nested message", methodName);

        message = message.isEmpty() ? methodName : message + " " + methodName;
        Class<?> cl = SkifUtil.classForName(exceptionClass);
        if (cl == ImplementationException.class) {
            return new ImplementationException(message, cause);
        } else if (cl == FinderException.class) {
            return new FinderException(message).setFeilkode("feilkode").setFeilkodebeskrivelse("feilkodebeskrivelse");
        } else {
            try {
                Throwable t = (Throwable) cl.getConstructor(String.class, Throwable.class).newInstance(message.isEmpty() ? methodName : message + " " + methodName, cause);
                if (t instanceof RuntimeException) {
                    return (RuntimeException) t;
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



    private String callIndirect(List<String> callSpec, String exceptionClass, String message, String method) throws SkifException {
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
