package no.statkart.skif.skiftest.service.testd;

import no.statkart.skif.exception.SkifException;

import java.util.List;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface DService {
    String noTx(String exceptionClass, String message) throws SkifException;
    String noTxNested(String exceptionClass, String message) throws SkifException;
    String requiresTx(String exceptionClass, String message) throws SkifException;
    String newTx(String exceptionClass, String message) throws SkifException;
    String nonMappedWSCall(String exceptionClass, String message)throws SkifException;
    String nonMappedEJBCall(String exceptionClass, String message)throws SkifException;
    String indirectNoTx(List<String> callSpec, String exceptionClass, String message) throws SkifException;
    String indirectRequiresTx(List<String> callSpec, String exceptionClass, String message) throws SkifException;
    String indirectNewTx(List<String> callSpec, String exceptionClass, String message) throws SkifException;
    String indirectNoEx(List<String> callSpec, String exceptionClass, String message);
}
