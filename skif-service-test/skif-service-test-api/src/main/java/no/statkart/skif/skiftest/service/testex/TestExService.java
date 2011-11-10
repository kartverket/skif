package no.statkart.skif.skiftest.service.testex;

import no.statkart.skif.skiftest.exception.SimpleException;
import no.statkart.skif.skiftest.exception.SimpleNonMappedException;

import java.util.List;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface TestExService {
    String noTx(String exceptionClass, String message) throws SimpleException, SimpleNonMappedException;
    String requiresTx(String exceptionClass, String message) throws SimpleException, SimpleNonMappedException;
    String newTx(String exceptionClass, String message) throws SimpleException, SimpleNonMappedException;
    String nonMappedCall(String exceptionClass, String message)throws SimpleException, SimpleNonMappedException;

    String indirectNoTx(List<String> callSpec, String exceptionClass, String message) throws SimpleException, SimpleNonMappedException;
    String indirectRequiresTx(List<String> callSpec, String exceptionClass, String message) throws SimpleException, SimpleNonMappedException;
    String indirectNewTx(List<String> callSpec, String exceptionClass, String message) throws SimpleException, SimpleNonMappedException;
    String indirectNoEx(List<String> callSpec, String exceptionClass, String message);
}
