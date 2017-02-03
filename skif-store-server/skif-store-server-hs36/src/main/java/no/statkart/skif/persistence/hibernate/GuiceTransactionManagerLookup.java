package no.statkart.skif.persistence.hibernate;

import com.google.inject.Injector;
import org.hibernate.HibernateException;
import org.hibernate.transaction.TransactionManagerLookup;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import java.util.Properties;

/**
 * Forsyner Hibernate med TransactionManager fra Guice.
 */
public class GuiceTransactionManagerLookup implements TransactionManagerLookup {
    @Override
    public TransactionManager getTransactionManager(Properties props) throws HibernateException {
        Injector injector = (Injector) props.get(Injector.class.getName());
        return injector.getInstance(TransactionManager.class);
    }

    @Override
    public String getUserTransactionName() {
        return null;
    }

    @Override
    public Object getTransactionIdentifier(Transaction transaction) {
        return transaction;
    }
}
