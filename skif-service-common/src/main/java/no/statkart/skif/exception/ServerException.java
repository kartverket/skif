package no.statkart.skif.exception;

import java.io.PrintWriter;

/**
 * Placeholder exception for exceptions kastet ifra server.
 *
 * Klassen skal ikke eksponeres ut.
 *
 * Denne blir benyttet for å representere nested exceptions på server uavhenging av hvilke bibliotek exceptions blir kastet ifra.
 *
 *
 * @author Leif Lislegård
 * @since 2.0
 */
public class ServerException extends RuntimeException {


    String stacktraceString;


    public ServerException() {
    }

    public ServerException(String message) {
        super(message);
    }

    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerException(Throwable cause) {
        super(cause);
    }


    @Override
    public void printStackTrace(PrintWriter s) {
        s.print(stacktraceString);
    }

    public String getStacktraceString() {
        return stacktraceString;
    }

    public void setStacktraceString(String stacktraceString) {
        this.stacktraceString = stacktraceString;
    }

    
}
