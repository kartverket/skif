package no.statkart.skif.service.module.server;

import com.google.inject.Binder;
import no.statkart.skif.exception.ImplementationException;
import org.slf4j.LoggerFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class ServerModuleStrategyJEE extends ServerModuleStrategy {

    @Override
    public void configure(Binder binder) {
        InitialContext initialContext = null;
        try {
            initialContext = new InitialContext();
            TransactionManager transactionManager = (TransactionManager) initialContext.lookup("javax.transaction.TransactionManager");
            binder.bind(TransactionManager.class).toInstance(transactionManager);
            UserTransaction userTransaction = (UserTransaction) initialContext.lookup("javax.transaction.UserTransaction");
            binder.bind(UserTransaction.class).toInstance(userTransaction);
        } catch (NamingException e) {
            throw new ImplementationException("Could not find TransactionManager", e);
        } finally {
            if (initialContext != null) {
                try {
                    initialContext.close();
                } catch (NamingException e) {
                    LoggerFactory.getLogger(getClass()).error("Error during InitialContext.close()", e);
                }
            }
        }
    }
}
