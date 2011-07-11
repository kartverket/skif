package no.statkart.skif.exception;

import org.slf4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: frehen
 * Date: 02.07.11
 * Time: 09:28
 * To change this template use File | Settings | File Templates.
 */
public class ReflectionException extends ImplementationException {

    public ReflectionException(String message) {
        super(message);
    }

    public ReflectionException(String message, Logger logger) {
        super(message, logger);
    }

    public ReflectionException(Throwable throwable) {
        super(throwable);
    }

    public ReflectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReflectionException(String message, Throwable cause, Logger logger) {
        super(message, cause, logger);
    }
}
