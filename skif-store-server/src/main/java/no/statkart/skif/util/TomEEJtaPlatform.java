package no.statkart.skif.util;

import org.apache.openejb.OpenEJB;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.ContainerSystem;
import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;

import javax.naming.NamingException;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.UserTransaction;

/**
 * Det følger med en OpenEJBJtaPlatform2 i TomEE, men siden den arver fra AbstractJtaPlatform, som ikke følger med, så
 * er det ikke mulig å bruke den klassen. Det hjelper ikke at AbstractJtaPlatform følger med SKIF, på grunn av måten
 * class loaders virker i JEE. Gjenskaper derfor OpenEJBJtaPlatform2 her slik at applikasjoner som bruke TomEE kan bruke
 * denne fremfor å lage sin egen greie.
 */
public class TomEEJtaPlatform extends AbstractJtaPlatform {
    public TomEEJtaPlatform() {
    }

    protected TransactionManager locateTransactionManager() {
        return OpenEJB.getTransactionManager();
    }

    protected UserTransaction locateUserTransaction() {
        try {
            return (UserTransaction) SystemInstance.get().getComponent(ContainerSystem.class).getJNDIContext().lookup("comp/UserTransaction");
        } catch (NamingException e) {
            return null;
        }
    }
}
