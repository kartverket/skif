package no.statkart.skif.service.jta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;

/**
 * Helper class for registering a jta synchronizable with the current transaction. The current
 * implementation is weblogic specific. The method should only be called insided an ejb having an
 * active transaction.</p>
 *                      
 * @author Henrik Fredholm
 */
public class JTATransactionHelper {
    private static Logger logger = LoggerFactory.getLogger(JTATransactionHelper.class);

   public static Transaction registerSynchronizationIfJTATransaction(JTASynchronizable jtaSynchronizable) {
      try {
         Transaction t = weblogic.transaction.TransactionHelper.getTransactionHelper().getTransaction();
         if (t!=null) {
            t.registerSynchronization(jtaSynchronizable);
            jtaSynchronizable.setWaitForJTASynchronization(true);
            logger.debug("Object registered with JTA transaction {}", jtaSynchronizable);
         }
         return t;
      } catch( RollbackException e ) {
         throw new RuntimeException("Unexpected Exception while attaching to current JTA transaction", e);
      } catch( IllegalStateException e ) {
         throw new RuntimeException("Unexpected Exception while attaching to current JTA transaction", e);
      } catch( SystemException e ) {
         throw new RuntimeException("Unexpected Exception while attaching to current JTA transaction", e);
      }
   }

   public static Transaction getJTATransaction() {
      return weblogic.transaction.TransactionHelper.getTransactionHelper().getTransaction();
   }
}

