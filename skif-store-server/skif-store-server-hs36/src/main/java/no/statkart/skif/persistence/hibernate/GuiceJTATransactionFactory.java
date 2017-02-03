package no.statkart.skif.persistence.hibernate;

import com.google.inject.Injector;
import org.hibernate.HibernateException;
import org.hibernate.transaction.JTATransactionFactory;

import javax.transaction.UserTransaction;
import java.util.Properties;

/**
 * Binder Hibernate til vår Single-VM UserTransaction. Dette er ikke nødevendigvis {@link no.statkart.skif.persistence.SingleVmUserTransaction}.
 */
public class GuiceJTATransactionFactory extends JTATransactionFactory {
    private Injector injector;

    @Override
    public void configure(Properties props) throws HibernateException {
        injector = (Injector) props.get(Injector.class.getName());
    }

    @Override
    protected UserTransaction getUserTransaction() {
        return injector.getInstance(UserTransaction.class);
    }
}
